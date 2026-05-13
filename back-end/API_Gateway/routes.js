const services = {
    auth: "http://localhost:8055",
    clientes: "http://localhost:4729",
    contas: "http://localhost:4873",
    gerentes: "http://localhost:2563"
};

const routeMappings = {
    '/login': {
        service: services.auth,
        redirect: "/auth/login",
        public: true,
        profiles: '*'
    },
    '/logout': {
        service: services.auth,
        redirect: "/auth/logout",
        public: true,
        profiles: '*'
    },

}