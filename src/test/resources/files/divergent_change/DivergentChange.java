public class DivergentChange {
    private Service service = new Service();

    public void exec() { // Noncompliant {{Divergent change}}
        service.a();
        service.b();
        service.c();
        service.d();
        service.a();
        service.b();
        service.c();
        service.d();
        service.a();
        service.b();
        service.c();
        service.d();
        service.a();
        service.b();
        service.c();
        service.d();
        service.a();
        service.b();
        service.c();
        service.d();
    }
}

class Service {
    public void a() {
    }

    public void b() {
    }

    public void c() {
    }

    public void d() {
    }
}
