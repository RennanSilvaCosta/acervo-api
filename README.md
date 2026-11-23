Back end do app "acervo", aplicação para registrar livros do seu acervo/biblioteca pessoal a partir do código ISBN

## Ambientes

A aplicação usa Spring profiles. O padrão é `local`; a imagem Docker já sobe com `prod`.

| Profile | Arquivo | Uso |
|---|---|---|
| `local` (padrão) | `application-local.properties` | Desenvolvimento, Postgres na própria máquina, sem SSL, SQL no log |
| `prod` | `application-prod.properties` | Produção, tudo por variável de ambiente (sem padrão), SSL no banco |

### Rodar localmente

Requisitos: Java 21 e Postgres instalado, com um banco chamado `acervo` criado.

1. Copie `.env.example` para `.env` e preencha o que for diferente do padrão (usuário/senha do Postgres, chave do Google Books). O `.env` não é versionado.
2. Suba a aplicação:

```bash
./mvnw spring-boot:run
```

Padrões do profile `local`: `localhost:5432`, banco `acervo`, usuário `postgres`, senha `postgres`.
Sem `GOOGLE_BOOKS_API_KEY`, a busca usa apenas a Open Library.

### Produção

Variáveis obrigatórias (a aplicação não sobe se faltar alguma): `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `GOOGLE_BOOKS_API_KEY`. Opcional: `PORT` (padrão 8080).

Para rodar o profile de produção fora do Docker: `SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run`.
