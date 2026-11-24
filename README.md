# InterBank Angola

Estou a desenvolver o InterBank Angola para a disciplina de Sistemas Distribuídos II. Quero simular uma transferência entre dois bancos fictícios. No fluxo que defini, o Banco Kwanza inicia a transferência e o Banco Horizonte valida a conta de destino e regista o crédito. Vou usar uma base de dados PostgreSQL para cada banco e uma API REST para a comunicação entre eles.

## Fase 1 — Preparação

- Preparei o guia de instalação e o diagrama de arquitectura. Vou mantê-los na pasta local `documentation/` até à entrega final.
- Guardei em `database/` os scripts de preparação das bases de dados.

## Fase 2 — Backends dos bancos

Criei duas aplicações Spring Boot independentes. Uso o JDK 23 instalado no Windows e o Maven Wrapper de cada banco para descarregar o Maven e as dependências. Mantive o Java 21 como alvo de compilação para que os ficheiros JAR também possam correr no Java 21. [Requisitos do Spring Boot](https://docs.spring.io/spring-boot/4.0/system-requirements.html)

No CMD do VS Code, selecciono o JDK 23 e confirmo a versão.

```bat
set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version
javac -version
```

Estas definições aplicam-se ao CMD em que as executaste. Repete-as no segundo terminal se ele ainda encontrar outra versão.

### Arrancar o Banco Kwanza

No CMD, a partir da raiz do repositório, executa os comandos abaixo. Substitui o valor de exemplo pela palavra-passe que definiste para `interbank_kwanza`.

```bat
cd banco-kwanza
set "KWANZA_DB_PASSWORD=PALAVRA_PASSE_DO_KWANZA"
mvnw.cmd -v
mvnw.cmd spring-boot:run
```

### Arrancar o Banco Horizonte

Num segundo CMD, a partir da raiz do repositório, usa a palavra-passe de `interbank_horizonte`.

```bat
cd banco-horizonte
set "HORIZONTE_DB_PASSWORD=PALAVRA_PASSE_DO_HORIZONTE"
mvnw.cmd spring-boot:run
```

O Flyway cria as tabelas `users` e `accounts` na base de cada banco no primeiro arranque. Configurei a porta PostgreSQL `5433` por omissão. Se usares outra porta, define `INTERBANK_DB_PORT` no CMD antes de arrancar. As palavras-passe ficam nas variáveis dos terminais.

### Experimentar as APIs

Num terceiro CMD, com os servidores activos, consulta a lista do Kwanza e testa a consulta de uma conta em cada banco.

```bat
curl.exe -i http://127.0.0.1:8081/api/accounts
curl.exe -i http://127.0.0.1:8081/api/accounts/1
curl.exe -i http://127.0.0.1:8082/api/accounts/1
```

Com as bases vazias, a lista devolve `200` e `[]`. As consultas por ID devolvem `404` até existir uma conta com esse ID. Os serviços escutam apenas no próprio computador durante esta fase.

Para experimentar uma conta real da simulação, abre o Query Tool do pgAdmin em `banco_kwanza` após o primeiro arranque. Executa o exemplo uma vez.

```sql
WITH titular AS (
    INSERT INTO users (name, email, role)
    VALUES ('Cliente de Teste', 'cliente@exemplo.test', 'CUSTOMER')
    RETURNING id
)
INSERT INTO accounts (account_number, owner_id, balance, available_balance, status)
SELECT 'BK-000123', id, 100000.00, 100000.00, 'ACTIVE'
FROM titular
RETURNING id;
```

Repete a consulta a `/api/accounts` para ver a conta. Para consultar directamente pelo ID, usa o valor devolvido pelo pgAdmin. Podes repetir o exemplo em `banco_horizonte` com o número `BH-000245` e consultar a conta na porta `8082`.

## Fase 3 — Transferências entre os bancos

Implementei transferências em kwanzas (`AOA`) do Banco Kwanza para o Banco Horizonte. A migração `V2` cria as tabelas de transferências e movimentos. Quando recebo um pedido no Kwanza, reservo o montante numa transacção local. Depois envio o pedido ao Horizonte por HTTP. Quando o Horizonte confirma o crédito, finalizo o débito no Kwanza. Uma recusa definitiva liberta a reserva. Se o resultado for incerto, a transferência continua `PENDING` e posso reenviar o pedido com a mesma chave.

### Testar no Postman

Para testar os endereços locais, uso a aplicação Postman para Windows ou selecciono o **Desktop Agent** no rodapé do Postman Web. [Guia dos agentes do Postman](https://learning.postman.com/docs/getting-started/basics/about-postman-agent/)

1. Arranca os dois backends no CMD como na Fase 2. No pgAdmin, confirma que tens uma conta activa com saldo no Kwanza e outra activa no Horizonte. Podes usar os exemplos da Fase 2. Usa o **ID** da conta de origem e o **número** da conta de destino.
2. No Postman, cria um pedido **POST** para `http://127.0.0.1:8081/api/transfers`. Em **Headers**, adiciona `Idempotency-Key` com um UUID. Podes usar `{{$guid}}` e depois guardar o ID devolvido, ou usar `e3722ec5-3117-4b1e-a824-79578bbdff31` no primeiro teste. Em **Body → raw → JSON**, envia:

   ```json
   {
     "sourceAccountId": 1,
     "destinationAccount": "BH-000245",
     "amount": 25000.00
   }
   ```

3. Confirma `200 OK` com `COMPLETED`. O saldo do Kwanza diminui e o do Horizonte aumenta pelo mesmo montante. Reenvia o mesmo pedido com o UUID devolvido no primeiro teste. Os saldos devem continuar iguais aos posteriores à transferência. Reutilizar esse UUID com um montante diferente devolve `409 Conflict`.
4. Consulta `GET /api/transfers` ou `GET /api/transfers/{id}` no Kwanza e `GET /api/interbank/credits/{id}` no Horizonte para ver o recibo.

| Cenário | Resultado |
| --- | --- |
| Conta de origem inexistente | `404` |
| Saldo disponível insuficiente ou conta de origem inactiva | `422` |
| Conta de destino inexistente ou inactiva | `200` com `FAILED` e reserva libertada |
| Horizonte indisponível | `202` com `PENDING` e montante reservado. Depois de o Horizonte voltar, reenvia o mesmo corpo e UUID. |

Abro o Swagger UI em [Kwanza](http://127.0.0.1:8081/swagger-ui/index.html) e [Horizonte](http://127.0.0.1:8082/swagger-ui/index.html). Cada banco publica o documento OpenAPI em `/v3/api-docs`, que posso importar no Postman. A autenticação entre bancos e a recuperação automática ficam para a fase de robustez.

## Plano de implementação

1. Preparei o ambiente, o repositório, os diagramas e as bases de dados.
2. Criei dois backends Spring Boot independentes, com entidades, repositórios e APIs de contas.
3. Implementei transferências via REST com reserva de saldo e idempotência. Documentei as APIs com OpenAPI e incluí um roteiro para experimentar no Postman.
4. Vou construir a aplicação Angular para demonstrar a autenticação, a consulta de contas, a criação de transferências e a consulta dos seus estados e histórico.
5. Vou usar Spring Security e JWT para proteger a aplicação e autenticar a comunicação entre os bancos. Também vou implementar a recuperação de operações pendentes e testar cenários de indisponibilidade, pedidos duplicados e operações simultâneas.
6. Vou preparar a demonstração, os diagramas finais, o relatório e a apresentação.

Depois da primeira entrega, vou implementar por etapas as evoluções da secção 17 do PDF. Planeio adicionar Spring Cloud Gateway, Docker Compose, RabbitMQ, notificações e transferências por número de telemóvel. Também vou criar um serviço central fictício de interoperabilidade, separar os módulos em quatro microserviços, adicionar observabilidade e integrar outros bancos fictícios.
