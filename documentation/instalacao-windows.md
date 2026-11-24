# Preparar o ambiente no Windows

Para correr o InterBank Angola no Windows, prepara o JDK, o Git, o PostgreSQL e o Node.js. Os backends usam Maven Wrapper e o frontend usa o npm incluído no Node.js. [Instalação do Spring Boot](https://docs.spring.io/spring-boot/installing.html)

## 1. Confirmar Java e Git

No CMD, executa os três comandos.

```bat
java -version
javac -version
git --version
```

Os backends compilam para Java 21. A versão base foi testada com o JDK 23. Para o Angular 21, confirma o Node.js 24 e o npm com `node --version` e `npm --version`. [Instalação do OpenJDK no Windows](https://learn.microsoft.com/en-gb/java/openjdk/install)

## 2. Instalar o PostgreSQL

1. Abre a página de downloads para Windows e segue a ligação para o instalador da EDB. Escolhe o PostgreSQL 18 para Windows x86-64. [Download oficial](https://www.postgresql.org/download/windows/)
2. Executa o instalador e selecciona **PostgreSQL Server** e **Command Line Tools**. O **pgAdmin 4** é opcional para consultas gráficas.
3. Mantém a pasta de instalação sugerida. Define e guarda a palavra-passe do utilizador administrador `postgres`.
4. Mantém a porta **5432** e a configuração regional sugerida. Podes fechar o **Stack Builder** no fim. [Passos do instalador da EDB](https://www.enterprisedb.com/docs/supported-open-source/postgresql/installing/windows/)

## 3. Criar as bases de dados

Abre o CMD na raiz do repositório. Se alteraste a pasta de instalação, ajusta o caminho de `psql.exe`. Usa a porta que escolheste no instalador. O exemplo assume a porta padrão 5432.

```bat
set "PSQL=C:\Program Files\PostgreSQL\18\bin\psql.exe"
set "PORTA=5432"
"%PSQL%" --version
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U postgres -d postgres -c "SELECT version();"
```

A primeira linha de verificação confirma a ferramenta de linha de comandos. A segunda confirma a ligação ao servidor. Introduz a palavra-passe de `postgres` quando for pedida.

Executa cada script uma vez numa instalação nova. Cada comando pede a palavra-passe de `postgres` e, durante o script, pede que definas e confirmes a palavra-passe do utilizador daquele banco. Guarda as duas palavras-passe de aplicação para arrancar os backends.

```bat
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U postgres -d postgres -f .\database\banco_kwanza.sql
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U postgres -d postgres -f .\database\banco_horizonte.sql
```

## 4. Verificar a separação

```bat
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U interbank_kwanza -d banco_kwanza -c "SELECT current_database(), current_user;"
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U interbank_horizonte -d banco_horizonte -c "SELECT current_database(), current_user;"
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U interbank_kwanza -d banco_horizonte -c "SELECT current_database();"
"%PSQL%" -h 127.0.0.1 -p %PORTA% -U interbank_horizonte -d banco_kwanza -c "SELECT current_database();"
```

Usa a palavra-passe do utilizador indicado em cada comando. Os dois primeiros devem mostrar a base e o utilizador correspondentes. Os dois últimos devem devolver um erro de permissão de acesso à base. O Flyway vai criar as tabelas no primeiro arranque dos backends.

## 5. Arrancar a aplicação

Se usaste a porta 5432, define a variável abaixo no CMD de cada banco antes do arranque. A configuração padrão do projecto usa 5433.

```bat
set "INTERBANK_DB_PORT=5432"
```

Segue as instruções do [README](../README.md) para configurar os segredos locais, arrancar os dois backends e abrir a interface Angular.
