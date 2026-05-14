export const services = {
    auth: "http://localhost:8055/auth",
    clientes: "http://localhost:4729",
    contas: "http://localhost:4873",
    gerentes: "http://localhost:2563"
};

export const routeMappings = {
    '/login': {
        target: services.auth,
        public: true,
        profiles: '*'
    },
    '/logout': {
        target: services.auth,
        public: true,
        profiles: '*'
    },
    '/gerente': {
        target: services.gerentes,
        public: true,
        profiles: '*'
    },
    '/clientes': {
        target: services.clientes,
        public: true,
        profiles: '*'
    },
    '/clientes/:cpf/aprovar': {
        target: services.clientes,
        public: false,
        profiles: 'GERENTE,ADMINISTRADOR'
    }
}