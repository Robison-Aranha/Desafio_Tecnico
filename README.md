# Desafio Técnico

![Static Badge](https://img.shields.io/badge/Back-SpringBoot-grenn)

![Static Badge](https://img.shields.io/badge/database-PostgreSQL-blue)


## Tecnologias
Escolhi o Spring para desenvolver a API por ser uma ferramenta robusta, simples de usar e altamente produtiva para construção de aplicações REST. Sua estrutura oferece suporte nativo para operações de CRUD, injeção de dependência e integração com diversos bancos de dados, o que torna o desenvolvimento mais ágil e organizado. Além disso, é um framework com o qual tenho bastante familiaridade e afinidade, o que contribuiu para minha escolha.

Para o banco de dados, optei pelo PostgreSQL, tanto pela praticidade de já tê-lo configurado na minha máquina quanto por suas qualidades técnicas. O PostgreSQL é um sistema de banco de dados relacional extremamente confiável, com forte conformidade com padrões SQL, suporte a transações ACID, extensibilidade e desempenho sólido mesmo com grandes volumes de dados. Esses fatores o tornam uma excelente escolha para aplicações de produção.


## Modelagem
O projeto foi modelado com base em três entidades principais: `User`, `Order` e `Product`, que representam respectivamente os usuários, seus pedidos e os produtos contidos em cada pedido. Essa estrutura reflete uma relação hierárquica e natural entre os dados:

**User**: entidade que representa o cliente. Um usuário pode possuir múltiplos pedidos.

**Order**: representa um pedido realizado por um usuário. Cada pedido pertence a um único usuário e contém um ou mais produtos.

**Product**: representa um produto comprado em um pedido. Está vinculado diretamente a um pedido.

As relações foram modeladas com `JPA (Jakarta Persistence API)` e mapeadas de forma bidirecional com anotações `@OneToMany`, `@ManyToOne` e `@JoinColumn`, garantindo a integridade entre as tabelas e permitindo uma navegação eficiente entre entidades.

Além disso, a API realiza o upload de arquivos com dados textuais, que são processados e convertidos em objetos intermediários `(UserMapperObj, OrderMapperObj e ProductMapperObj)`.

**Esses objetos são utilizados para:**

Eliminar duplicações por meio de filtragens personalizadas.

Criar as ligações corretas entre usuários, pedidos e produtos.

Persistir os dados no banco de forma organizada, garantindo que as entidades respeitem as constraints de chave estrangeira (por exemplo, não salvar um produto sem que seu pedido e usuário estejam corretamente definidos e salvos anteriormente).

**Durante esse processo, uma lógica cuidadosa foi aplicada para garantir a ordem de persistência correta:**

Primeiro os usuários são salvos.

Em seguida, os pedidos, já vinculados aos usuários persistidos.

Por fim, os produtos, com os pedidos já salvos vinculados.

## Arquitetura

O projeto foi desenvolvido seguindo uma `arquitetura em camadas`, separando claramente as responsabilidades entre as partes da aplicação. Essa abordagem promove organização, facilita a manutenção do código e torna o sistema mais escalável e testável. As principais camadas e suas responsabilidades são:

### Controller (camada de entrada)
Responsável por expor os endpoints da API REST. Recebe as requisições do cliente, delega > a lógica para os serviços e retorna as respostas adequadas. Nessa camada, também ocorre > a validação de entrada e tratamento de exceções específicas via ResponseStatusException.

### Service (camada de negócios):

**Contém a lógica de negócio da aplicação. Aqui é onde:**
O arquivo enviado é processado linha por linha.
Os dados são extraídos e convertidos em objetos intermediários (mappers).
As entidades são associadas corretamente e salvas no banco de dados.
As respostas são montadas para serem retornadas à camada de controle.

A lógica foi cuidadosamente organizada para garantir que a persistência ocorra em ordem correta (usuário → pedido → produto) e para evitar duplicidades através de verificações com o banco.

### Mapper
A camada de mapeamento (EntitysMapper) transforma objetos intermediários (como ParserData, UserMapperObj, etc.) em entidades JPA ou objetos de resposta (Response). Essa separação mantém a lógica de conversão isolada e reutilizável.

### Domain (entidades)
Contém os modelos que representam as tabelas do banco de dados. Cada classe (User, Order, Product) é anotada com JPA `(@Entity, @Id, @OneToMany, etc.)` e reflete diretamente a estrutura das tabelas, com relações entre si bem definidas.

### Repository (acesso a dados)
Interfaces que estendem JpaRepository, fornecendo uma maneira simples e eficiente de interagir com o banco de dados sem a necessidade de escrever SQL manualmente. Foram utilizados repositórios para User, Order e Product.

## endpoints da API

### Post `/file`
Descrição: Realiza o upload de um arquivo .txt contendo dados de usuários, pedidos e produtos.

**Parâmetro**:
`file (MultipartFile)`: Arquivo estruturado com os dados.

**Processo**:
Faz o parsing de cada linha do arquivo.

Cria entidades (User, Order, Product) e realiza os devidos vínculos entre elas.

Remove duplicatas.

Persiste os dados não duplicados e retorna os dados padronizados em JSON.

**Resposta**: Objeto ParsedFilesResponse com os dados que foram convertidos (usuários → pedidos → produtos).

### GET `/file/order`
**Descrição**: Retorna uma listagem paginada de pedidos, podendo filtrar por ID do pedido e/ou intervalo de datas.

Parâmetros de consulta (query params):

`order_id` (opcional): ID específico de um pedido.

`startDate` (opcional): Data inicial (formato numérico, ex: 20240101).

`endDate` (opcional): Data final (formato numérico, ex: 20241231).

`page`, `size`, `sort`: Padrão Spring Data Pageable.

**Resposta**: Página `(Page<OrderResponse>)` contendo os pedidos filtrados.
