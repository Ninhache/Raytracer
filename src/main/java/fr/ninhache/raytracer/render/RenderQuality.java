package fr.ninhache.raytracer.render;

public enum RenderQuality {
    PREVIEW(0.3),
    NORMAL(1.0),
    HIGH(2);

    private final double scaleFactor;

    RenderQuality(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return multiplicateur appliqué à la résolution (1.0 = résolution d'origine).
     */
    public double scaleFactor() {
        return scaleFactor;
    }

    @Override
    public String toString() {
        return switch (this) {
            case PREVIEW -> "Preview (x" + scaleFactor + ")";
            case NORMAL  -> "Normal (x" + scaleFactor + ")";
            case HIGH    -> "High (x" + scaleFactor + ")";
        };
    }
}
