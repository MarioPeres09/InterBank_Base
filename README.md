# InterBank Angola

Projecto académico de Sistemas Distribuídos II que simula transferências entre dois bancos fictícios. O Banco Kwanza inicia a transferência. O Banco Horizonte valida a conta de destino e regista o crédito. Cada banco usa a sua própria base de dados PostgreSQL e comunica com o outro por uma API REST.

## Fase 1 — Preparação

- O guia de instalação e o diagrama de arquitectura ficam na pasta local `documentation/` até à entrega final.
- `database/` contém os scripts de preparação das bases de dados.

## Plano de implementação

1. Preparar o ambiente, o repositório, os diagramas e as bases de dados.
2. Criar dois backends Spring Boot independentes, com entidades, repositórios e APIs de contas.
3. Implementar transferências REST, reserva de saldo e idempotência. Testar as APIs com Postman e documentá-las com OpenAPI.
4. Construir a aplicação Angular para autenticação, contas, transferências, estados e histórico.
5. Adicionar Spring Security/JWT, autenticação entre bancos e recuperação de operações pendentes. Testar indisponibilidade, duplicados e concorrência.
6. Preparar a demonstração, os diagramas finais, o relatório e a apresentação.

Depois da primeira entrega, implementar as evoluções da secção 17 do PDF em partes próprias. O plano inclui Spring Cloud Gateway, Docker Compose, RabbitMQ, notificações, transferências por número de telemóvel, serviço central fictício de interoperabilidade, separação em quatro microserviços, observabilidade e integração com outros bancos fictícios.
