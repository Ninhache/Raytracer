package fr.ninhache.raytracer.math;

public final class TestVec extends AbstractVec3 {
    public TestVec(double x, double y, double z) { super(x, y, z); }
    @Override protected TestVec ofSameType(double x, double y, double z) { return new TestVec(x, y, z); }

    // Expose schur publiquement pour les tests
    public TestVec schurPublic(TestVec other) {
        return (TestVec) schur(other);
    }
}
