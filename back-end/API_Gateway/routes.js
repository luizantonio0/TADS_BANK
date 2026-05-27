export const services = {
    auth: "http://ms-auth:8055",
    clientes: "http://ms-cliente:4729",
    contas: "http://ms-conta:4873",
    gerentes: "http://ms-gerente:2563"
};

export const routes = [
    { name: "reboot", method: 'GET', path: '/reboot', target: services.auth, public: true, profiles: '*' },

    { name: "login", method: 'POST', path: '/login', target: services.auth, public: true, profiles: '*' },
    { name: "logout", method: 'POST', path: '/logout', target: services.auth, public: false, profiles: '*' },

    { name: "autocadastro", method: 'POST', path: '/clientes', target: services.clientes, public: true, profiles: '*' },
    { name: "buscarClientes", method: 'GET',  path: '/clientes', target: services.clientes, public: false, profiles: 'ADMINISTRADOR,GERENTE' },
    { name: "aprovarCliente", method: 'POST', path: '/clientes/:cpf/aprovar', target: services.clientes, public: false, profiles: 'GERENTE' },
    { name: "rejeitarCliente", method: 'POST', path: '/clientes/:cpf/rejeitar', target: services.clientes, public: false, profiles: 'GERENTE' },
    { name: "buscarCliente", method: 'GET', path: '/clientes/:cpf', target: services.clientes, public: false, profiles: '*' },
    { name: "atualizarCliente", method: 'PUT', path: '/clientes/:cpf', target: services.clientes, public: false, profiles: 'CLIENTE' },

    { name: "criarGerente", method: 'POST', path: '/gerentes', target: services.gerentes, public: false, profiles: 'ADMINISTRADOR' },
    { name: "buscarGerentes", method: 'GET', path: '/gerentes', target: services.gerentes, public: false, profiles: 'ADMINISTRADOR' }, 

    { name: "buscarContas", method: 'GET', path: '/contas', target: services.contas, public: false, profiles: 'ADMINISTRADOR,GERENTE' },
    { name: "saldoConta", method: 'GET', path: '/contas/:conta/saldo', target: services.contas, public: false, profiles: '*' },
    { name: "transferirSaldo", method: 'POST', path: '/contas/:conta/transferir', target: services.contas, public: false, profiles: 'CLIENTE' },
    { name: "depositarSaldo", method: 'POST', path: '/contas/:conta/depositar', target: services.contas, public: false, profiles: 'CLIENTE' },
    { name: "sacarSaldo", method: 'POST', path: '/contas/:conta/sacar', target: services.contas, public: false, profiles: 'CLIENTE' },
    { name: "extrato", method: 'GET', path: '/contas/:conta/extrato', target: services.contas, public: false, profiles: 'CLIENTE' },
    { name: "depositar", method: 'POST', path: '/contas/:conta/depositar', target: services.contas, public: false, profiles: 'CLIENTE' },
    { name: "sacar", method: 'POST', path: '/contas/:conta/sacar', target: services.contas, public: false, profiles: 'CLIENTE' },
    { name: "extrato", method: 'GET', path: '/contas/:conta/extrato', target: services.contas, public: false, profiles: 'CLIENTE' }
]
