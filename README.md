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

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/users` | Cria usuário (`name` obrigatório, `email` único) |
| POST | `/books/isbn/{isbn}` | Busca o livro pelo ISBN-10 ou ISBN-13 (banco, Google Books e Open Library) e grava no catálogo |
| POST | `/users/{userId}/books/{bookId}` | Coloca o livro na estante. Corpo obrigatório: `{ "statusReading": "READING" }` |
| PATCH | `/users/{userId}/books/{bookId}` | Atualiza status, datas, página atual, nota, anotações e favorito |
| GET | `/users/{userId}/books` | Lista a estante, do mais recente para o mais antigo |
| GET | `/users/{userId}/books/{bookId}` | Detalhes do livro na estante (`book` + `reading`); 404 se o livro não estiver na estante |

### Status de leitura

| Valor | No app |
|---|---|
| `READ` | LIDO |
| `READING` | LENDO |
| `WANT_READ` | QUERO LER |
| `REREADING` | RELENDO |
| `ABANDONED` | ABANDONEI |

### Detalhes do livro

`GET /users/{userId}/books/{bookId}`

```json
{
  "book": {
    "id": 2,
    "title": "...",
    "synopsis": "...",
    "authors": ["..."],
    "publishers": ["..."],
    "publishYear": "2019",
    "pageCount": 320,
    "language": "pt",
    "isbn10": "0306406152",
    "isbn13": "9780306406157",
    "coverUrl": "..."
  },
  "reading": {
    "status": "READING",
    "favorite": false,
    "addedAt": "2026-11-24T12:00:00Z",
    "startedAt": null,
    "finishedAt": null,
    "currentPage": 120,
    "rating": null,
    "notes": null
  }
}
```

- `publishYear`: sempre só o ano, com quatro dígitos (ex.: "May 12, 2019" vira "2019"), ou `null`.
- `language`: código ISO 639-1 vindo do Google Books (ex.: `pt`, `en`), ou `null` quando a fonte não informa.
- `isbn10` / `isbn13`: `null` quando o livro não tem aquele formato registrado.
