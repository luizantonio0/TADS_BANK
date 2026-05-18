export const services = {
    auth: "http://ms-auth:8055",
    clientes: "http://ms-cliente:4729",
    contas: "http://ms-conta:4873",
    gerentes: "http://ms-gerente:2563"
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
    },
    '/clientes/:cpf': {
        target: services.clientes,
        public: false,
        profiles: 'GERENTE,ADMINISTRADOR,CLIENTE'
    }
}
