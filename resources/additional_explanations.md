This document explains the main design decisions that differ from the “naive” implementation usually expected in the assignment, and why these choices were made:

- the **parsing architecture**, using a registry of token handlers discovered via **reflection**;
- a **structured implementation** of ray reflection (standard recursive tracing with `maxdepth`);
- additional performance and usability features:
  - **BVH acceleration structure** for ray–scene intersections,
  - **multithreaded rendering**,
  - **render statistics** and a small JavaFX front-end.

The goal was to keep the observable behaviour compliant with the assignment, while making the codebase more modular, testable, and maintainable.

---

## 1. Overall architecture

### 1.1 Strict separation: parsing / scene / rendering

Instead of mixing parsing, scene construction and rendering logic in a single class, the project is split into several layers:
- **Parsing**:  
  `SceneLoader`, `ParsingContext`, `TokenProcessor` implementations.
- **Scene construction**:  
  `SceneBuilder` (Builder pattern).
- **Domain model**:  
  `Scene`, `Camera`, `IShape`, `ILight`, `Material`, etc.
- **Rendering**:  
  `Renderer`, `RayTracer`, `RenderResult`, `RenderStats`.

Benefits:
- Each layer has a clear responsibility.
- Parsing can be unit-tested without rendering.
- A scene can be built programmatically (via `SceneBuilder`) without going through the text parser, which is useful for tests or demo scenes.

From the point of view of the assignment, this is an **architectural refactor**, not a change in features.

---

## 2. Parsing: token handlers discovered by reflection

### 2.1 What the assignment usually implies

Most raytracer assignments show (explicitly or implicitly):
- a main parsing loop: `for (line : file)`,  
- a big `switch` / `if-else` on the first token (`"size"`, `"camera"`, `"sphere"`, `"tri"`, etc.),
- each case directly handling parsing and updating the scene.

This works, but has drawbacks:
- the parser becomes a “god method” that grows with every new command;
- it is hard to test single commands in isolation;
- adding a new primitive requires editing the main parser code.

### 2.2 What I implemented: a handler registry

I replaced the big `switch` with a more modular mechanism based on:
- a small interface:

  ```java
  public interface TokenProcessor {
      void process(String[] tokens, ParsingContext context) throws ParseException;
  }
````

* one implementation per command, for example:
  ```java
  @TokenHandler("ambient")
  public class AmbientTokenHandler implements TokenProcessor {
      @Override
      public void process(String[] tokens, ParsingContext context) throws ParseException {
          // parse r g b, validate, update ParsingContext / SceneBuilder
      }
  }
  ```

* a **registry** that maps:
  * the token name (e.g. `"ambient"`)
  * to the handler class that knows how to process it (`AmbientTokenHandler`).

### 2.3 Reflection-based discovery

The registry is not populated manually with `put("ambient", new AmbientTokenHandler())` lines.

Instead, at startup:
* the code scans a package (e.g. `fr.ninhache.raytracer.parser.handlers`);
* finds all classes annotated with `@TokenHandler("...")`;
* instantiates them via reflection;
* registers them in a `Map<String, TokenProcessor>`.

Pseudo-code:

```java
for (Class<?> clazz : findAllClassesIn("fr.ninhache.raytracer.parser.handlers")) {
    TokenHandler ann = clazz.getAnnotation(TokenHandler.class);
    if (ann != null && TokenProcessor.class.isAssignableFrom(clazz)) {
        String keyword = ann.value();
        TokenProcessor handler = (TokenProcessor) clazz.getDeclaredConstructor().newInstance();
        registry.put(keyword, handler);
    }
}
```

The main parsing loop becomes:

```java
String keyword = tokens[0];
TokenProcessor handler = registry.get(keyword);
if (handler == null) {
    throw new ParseException("Unknown command: " + keyword);
}
handler.process(tokens, context);
```

### 2.4 Why this is a deviation (and why it’s intentional)

Compared to the assignment, this is a deviation in **implementation style**, not in semantics:

* The **scene description language** is exactly the same.
* Only the way commands are dispatched to their logic is different.

Benefits:

* Adding a new command = creating a new annotated class.
  No need to touch a central `switch`.
* Each handler is **independent** and **unit-testable**.
* The parser core remains short and readable.

This is a standard use of **Java reflection** to reduce coupling and improve extensibility, while staying 100 % compatible with the textual format and constraints of the assignment.

---

## 3. Ray reflection (recursive tracing and maxdepth)

### 3.1 What the assignment describes

In the later milestones, the assignment typically introduces:

* a `maxdepth` parameter to limit recursion,
* the reflection formula `r = d - 2 (d·n) n`,
* shooting a reflected ray from the intersection,
* combining local shading (Lambert + Phong) with reflected color.

The usual pseudo-code is something like:

* `traceRay(ray, depth)` where:
  * if `depth > maxdepth` -> return background (black),
  * else -> find nearest intersection,
  * if no hit -> return background,
  * otherwise:
    * compute local color (Lambert + Phong),
    * if material is reflective -> launch reflected ray with `depth + 1`,
    * mix local and reflected color.

### 3.2 What I implemented concretely

I followed this logic, with some structural choices:
* recursive function:

  ```java
  private Color traceRay(Ray ray, int depth, Scene scene)
  ```

* recursion is controlled with `depth` and `scene.getMaxDepth()`;
* at a hit point, the shading is split into:
  1. **local color**: Lambert + Phong, using:
     * surface normal,
     * hit point position,
     * light sources,
     * the object’s material;
  2. **reflected color**: if the material has reflection/specular:
     ```java
     Ray reflected = new Ray(hitPointOffset, reflectDirection);
     Color reflectedColor = traceRay(reflected, depth + 1, scene);
     ```
* final color is a mix of those two contributions, for example:
  ```java
  Color result = localColor.mul(1 - kr).add(reflectedColor.mul(kr));
  ```
  where `kr` is a reflection coefficient derived from the material.

### 3.3 Small differences vs. the text

Relative to the written assignment, the differences are mostly **code organization**, not behaviour:
* `maxdepth` is stored in `Scene` rather than as a global static variable.
* The epsilon offset for the origin of the reflected ray (`P + ε·N`) is handled in one place to avoid self-intersection artifacts.
* The logic “is this material reflective?” can be encoded either as a dedicated coefficient or derived from the specular term.

The visually observable behaviour is the same:
* `maxdepth = 0` -> no reflections,
* `maxdepth > 0` -> reflections appear, up to the specified recursion depth,
* reflection strength is modulated by the material.

---

## 4. BVH, multithreading and render statistics (extensions)

The base assignment typically assumes a simple, brute-force intersection loop:
* for each ray, test intersection against **all** shapes.

This is correct but slow for larger scenes.
I added several **optional performance and tooling features** on top of the required functionality:
* a **BVH (Bounding Volume Hierarchy)** acceleration structure,
* **multithreaded rendering**,
* **render statistics** to observe performance.

None of these change the visible results of the raytracer; they only affect how fast we reach those results and what kind of feedback we can provide.

### 4.1 BVH acceleration structure

#### Idea

A BVH (Bounding Volume Hierarchy) is a tree that groups shapes into nested bounding boxes:
* each internal node stores a bounding box (e.g. an AABB) that encloses its children;
* each leaf node contains one or a few shapes.

When tracing a ray:
* instead of testing the ray against all shapes,
* we first intersect the ray with the BVH root’s bounding box,
* then recursively traverse only the branches whose bounding boxes are hit,
* skipping entire subsets of objects at once.

#### Implementation outline

At scene construction time:
* collect all shapes in a list,
* build a BVH tree from that list (e.g. splitting along the largest axis, median partition, etc.),
* store the resulting BVH in the `Scene` (or a dedicated acceleration structure field).

At render time:
* the raytracer asks the BVH for the closest intersection,
* the BVH traversal replaces the naive “for each shape: intersect” loop.

From the outside:
* the `Scene` still exposes a method like `findClosestIntersection(ray)`;
  internally, this now delegates to the BVH.

#### Why this is a deviation

The assignment usually stops at brute-force intersection.
Adding a BVH is an **optimization / bonus feature** that:
* significantly reduces intersection cost for complex scenes,
* keeps the API (`findClosestIntersection`) unchanged,
* is entirely optional: if the BVH is disabled or not built, we can fall back to the naive loop.

---

### 4.2 Multithreaded rendering

Rendering an image is **embarrassingly parallel**:
* each pixel can be computed independently,
* so we can distribute rows or tiles across several threads.

Implementation choice:

* `Renderer.render(Scene)` remains the single entry point;
* internally, it uses a **thread pool** (`ExecutorService`) to:
  * create tasks (e.g. one per row or block of rows),
  * submit them to the pool,
  * wait for all tasks to complete,
  * assemble the final `BufferedImage`.

The BVH structure is read-only once built, which makes it safe to use from multiple threads concurrently without additional synchronization.

Benefits:
* performance scales with the number of CPU cores,
* CPU is better utilized for high-resolution / high-depth renders,
* the external API (`Renderer.render(scene)`) stays unchanged.

---

### 4.3 Render statistics

To better understand and debug performance, the renderer returns not only the image but also a set of statistics:

* `RenderResult` contains:
  * the rendered `BufferedImage`,
  * a `RenderStats` object.

`RenderStats` may include, for example:

* total render time,
* approximate number of primary and secondary rays,
* rays per pixel,
* number of threads used,
* possibly information related to BVH usage (e.g. node visits).

These stats are used both in:

* the CLI (printed to the console),
* and the JavaFX UI (displayed alongside the rendered image).

This is not required by the assignment but is very useful to:

* evaluate the benefit of the BVH and multithreading,
* compare different scenes,
* tune settings like `maxdepth` or image resolution.

---

## 5. JavaFX integration (interactive usage)

In addition to the classic “offline” mode (CLI that reads a scene file and writes a PNG), a **JavaFX UI** has been added to:

* open scene files,
* trigger renders from the UI,
* display the resulting image and associated stats.

This UI reuses the same core components:

* `SceneLoader` to parse the file,
* `Scene` as the in-memory representation,
* `Renderer` to perform the raytracing,
* `RenderResult` to carry image + stats.

This shows that the internal architecture (clean separation of parsing / model / rendering) is flexible enough to support both:

* a classic TP-style command-line interface,
* a more interactive, real-time experimentation environment.

---

## Conclusion

The main deviations from the “most straightforward” assignment implementation are:

1. **Reflection-based token handler registry** instead of a big `switch` in the parser:
   * same scene file syntax,
   * more modular, extensible, and testable.

2. **Structured reflection handling in the ray tracer**:
   * recursive `traceRay`, `maxdepth` stored in `Scene`,
   * clear combination of local shading and reflected color.

3. **Extensions**: multithreading, render statistics, JavaFX viewer:
   * optional features, not required by the statement,
   * built on top of the same core architecture, without changing the expected outputs.

These choices keep the behaviour required by the assignment while improving code quality and making the raytracer a more realistic and reusable rendering engine.
