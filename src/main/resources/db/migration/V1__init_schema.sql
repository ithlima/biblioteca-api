CREATE TABLE usuarios (
    id binary(16) NOT NULL,
    nome_completo varchar(150) NOT NULL,
    email varchar(150) NOT NULL,
    telefone_whatsapp varchar(20),
    senha varchar(255) NOT NULL,
    role varchar(20) NOT NULL,
    ativo bit(1) NOT NULL,
    login_bloqueado bit(1) NOT NULL,
    email_validado bit(1) NOT NULL,
    criado_em datetime(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE categorias (
    id binary(16) NOT NULL,
    nome varchar(100) NOT NULL,
    criado_em datetime(6) NOT NULL,
    editado_em datetime(6),
    PRIMARY KEY (id),
    UNIQUE KEY (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE livros (
    id binary(16) NOT NULL,
    nome_obra varchar(180) NOT NULL,
    autor varchar(120) NOT NULL,
    editora varchar(120),
    volume varchar(50),
    descricao text,
    quantidade int NOT NULL,
    foto_capa_url varchar(500),
    oculto bit(1) NOT NULL,
    motivo_ocultacao varchar(500),
    criado_por_id binary(16),
    editado_por_id binary(16),
    criado_em datetime(6) NOT NULL,
    editado_em datetime(6),
    version bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (criado_por_id) REFERENCES usuarios(id),
    FOREIGN KEY (editado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE livro_categoria (
    livro_id binary(16) NOT NULL,
    categoria_id binary(16) NOT NULL,
    PRIMARY KEY (livro_id, categoria_id),
    FOREIGN KEY (livro_id) REFERENCES livros(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE emprestimos (
    id binary(16) NOT NULL,
    livro_id binary(16) NOT NULL,
    leitor_id binary(16) NOT NULL,
    data_pedido datetime(6) NOT NULL,
    data_emprestimo date,
    data_devolucao_prevista date,
    data_devolucao_real date,
    quantidade_renovacoes int NOT NULL,
    status varchar(30) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (livro_id) REFERENCES livros(id),
    FOREIGN KEY (leitor_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE penalidades (
    id binary(16) NOT NULL,
    usuario_id binary(16) NOT NULL,
    emprestimo_id binary(16) NOT NULL,
    motivo varchar(255) NOT NULL,
    data_inicio date NOT NULL,
    data_fim date NOT NULL,
    ativa bit(1) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (emprestimo_id) REFERENCES emprestimos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE codigos_verificacao (
    id binary(16) NOT NULL,
    email varchar(150) NOT NULL,
    codigo varchar(10) NOT NULL,
    tipo varchar(30) NOT NULL,
    criado_em datetime(6) NOT NULL,
    expira_em datetime(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
