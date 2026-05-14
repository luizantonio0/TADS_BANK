db = db.getSiblingDB('ms_auth');

db.credentials.deleteMany({});

db.credentials.insertMany([
  {
    _id: "98574307084",
    email: "ger1@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "GERENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "64065268052",
    email: "ger2@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "GERENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "23862179060",
    email: "ger3@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "GERENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "40501740066",
    email: "adm1@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "ADMINISTRADOR",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "12912861012",
    email: "cli1@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "CLIENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "09506382000",
    email: "cli2@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "CLIENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "85733854057",
    email: "cli3@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "CLIENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "58872160006",
    email: "cli4@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "CLIENTE",
    _class: "com.bantads.auth.document.Credentials"
  },
  {
    _id: "76179646090",
    email: "cli5@bantads.com.br",
    password: "$2a$12$ErTAzddLw07oe9DtGL1QcO6RGMYjfNF2pCcZpbcCs2gdlqsT5S1l6",
    profile: "CLIENTE",
    _class: "com.bantads.auth.document.Credentials"
  }
]);