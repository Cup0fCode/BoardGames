package water.of.cup.boardgames.extension;

public class MethodSignature {
    private final String name;
    private final Class<?>[] params;

    protected MethodSignature(String name, Class<?>[] params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParams() {
        return params;
    }
}
