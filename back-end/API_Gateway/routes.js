export const services = {
    auth: "http://ms-auth:8055",
    clientes: "http://ms-cliente:4729",
    contas: "http://ms-conta:4873",
    gerentes: "http://ms-gerente:2563"
};

export const routes = [
    { method: 'POST', path: '/login', target: services.auth, public: true, profiles: '*' },
    { method: 'POST', path: '/logout', target: services.auth, public: false, profiles: '*' },

    { method: 'POST', path: '/clientes', target: services.clientes, public: true, profiles: '*' },
    { method: 'GET',  path: '/clientes', target: services.clientes, public: false, profiles: 'ADMINISTRADOR,GERENTE' },
    { method: 'POST', path: '/clientes/:cpf/aprovar', target: services.clientes, public: false, profiles: 'GERENTE' },
    { method: 'POST', path: '/clientes/:cpf', target: services.clientes, public: false, profiles: 'GERENTE,ADMINISTRADOR' },

    { method: 'POST', path: '/gerente', target: services.gerentes, public: false, profiles: 'ADMINISTRADOR' },
]