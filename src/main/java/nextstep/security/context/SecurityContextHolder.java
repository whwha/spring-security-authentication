package nextstep.security.context;

public class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> contextHolder;

    static {
        contextHolder = new ThreadLocal<>();
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static SecurityContext getContext() {
        SecurityContext context = contextHolder.get();
        if (context == null) {
            context = createEmptyContext();
            contextHolder.set(context);
        }
        return context;
    }

    public static void setContext(SecurityContext context) {
        if (context != null) {
            contextHolder.set(context);
        }
    }

    public static SecurityContext createEmptyContext() {
        return new SecurityContext();
    }
}
