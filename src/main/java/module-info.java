// Module name mirrors the root package rather than the automatic name javac derives from the jar
// filename ("java.properties"). That derived name sits in the java.* namespace the JDK reserves for
// platform modules: it compiles and runs today, measured against a five-module consumer on JDK 25,
// and only because that consumer packages into one jar and starts from the classpath, where the
// runtime never resolves the name at all.
// See docs/project/decisions/2026-09-07-real-module-descriptor-instead-of-automatic-name.md.
module com.github.zrdj.java.properties {
    requires org.slf4j;

    exports com.github.zrdj.java.properties;
    exports com.github.zrdj.java.properties.error;
    exports com.github.zrdj.java.properties.naming;
    exports com.github.zrdj.java.properties.store;
}
