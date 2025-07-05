# 🚗 AutoFácil - Sistema de Locadora de Veículos
Versão: 2.0-modern

Um sistema de desktop completo para gerenciamento de uma locadora de veículos, desenvolvido em Java com uma interface gráfica moderna. O sistema cobre todas as operações essenciais do negócio, desde o cadastro de clientes e veículos até o registro e finalização de aluguéis, com regras de negócio bem definidas.

## ✨ Funcionalidades Principais
- **Dashboard Intuitivo**: Tela inicial com cartões que exibem estatísticas chave em tempo real, como veículos disponíveis, aluguéis ativos e total de clientes cadastrados.
- **Gerenciamento de Veículos com Integração FIPE**:
    - Adição de novos veículos com busca automática de marca, modelo e ano através da API da FIPE.
    - O valor FIPE é armazenado, e o preço da diária é sugerido como 1% desse valor, automatizando a precificação.
    - Visualização da frota em cartões, com status de "Disponível" ou "Alugado".
- **Gerenciamento de Clientes**:
    - Cadastro e edição de clientes com validação de CPF, e-mail e telefone.
    - Formatação automática de campos (CPF e telefone) para melhor experiência do usuário.
- **Gerenciamento de Aluguéis**:
    - Registro de saída e devolução de veículos em uma interface clara.
    - **Cálculo de Multa por Atraso**: Aplicação de juros compostos de 10% ao dia sobre o valor base do aluguel em caso de atraso na devolução.
    - Histórico de todos os aluguéis finalizados, com detalhes sobre valor total e multas aplicadas.
- **Interface Gráfica Moderna**:
    - Tema escuro e componentes estilizados fornecidos pela biblioteca FlatLaf.
    - Design centralizado na classe `UIDesigner` para garantir consistência visual.

## 🛠️ Tecnologias e Dependências
O projeto é construído com Java e gerenciado pelo Maven. As principais dependências são:

| Dependência | Versão | Propósito |
| :--- | :--- | :--- |
| **Java** | 21 | Linguagem principal do projeto. |
| **FlatLaf** | 3.4.1 | Biblioteca de *Look and Feel* que fornece a interface gráfica moderna e o tema escuro. |
| **LGoodDatePicker** | 11.2.1 | Componente de calendário moderno e amigável para seleção de datas. |
| **Gson** | 2.10.1 | Biblioteca do Google para serialização e desserialização de dados no formato JSON. |
| **OkHttp** | 4.12.0 | Cliente HTTP para realizar as chamadas à API da FIPE de forma eficiente. |
| **JUnit** | 4.11 | Framework para a escrita e execução de testes de unidade. |

<hr>

## 📦 Estrutura do Projeto
O projeto segue uma arquitetura bem definida, separando as responsabilidades em diferentes pacotes. Abaixo está a árvore completa de arquivos e diretórios:

### 🔹 Arquivos na Raiz

├── .gitignore                       # Arquivos e pastas ignorados pelo Git

├── pom.xml                          # Arquivo principal do Maven (build, dependências, plugins)

└── dependency-reduced-pom.xml       # Pom gerado após o empacotamento (shaded JAR)



### 📁 registros (aparecem assim que dados forem adicionados)
Arquivos JSON com os dados persistidos da aplicação:

├── alugueis.json

├── clientes.json

└── veiculos.json


### 📁 src
Dividido em main e test -> o main serve para armazenar o projeto estável, enquanto o main é destinado para testes
No momento em que clonar o repositório, ambas estruturas estarão indênticas

└── main/test

    ├── java

    │   └── br.univates.universo

    │       ├── Main.java                    # Classe principal

    │

    │       ├── core                         # Modelos de domínio

    │       │   ├── Aluguel.java

    │       │   ├── Cliente.java

    │       │   └── Veiculo.java

    │

    │       ├── data                         # Camada de persistência

    │       │   ├── GerenciadorAlugueis.java

    │       │   ├── GerenciadorClientes.java

    │       │   ├── GerenciadorVeiculos.java

    │       │   └── JsonDataManager.java

    │

    │       ├── gui                          # Interface gráfica (Swing)

    │       │   ├── JanelaPrincipal.java

    │       │   ├── PainelDashboard.java

    │       │   ├── PainelGerenciamentoAlugueis.java

    │       │   ├── PainelGerenciamentoClientes.java

    │       │   └── PainelGerenciamentoVeiculos.java

    │

    │       └── util                         # Utilitários e validadores

    │           ├── CpfDocumentFilter.java

    │           ├── CpfValidator.java

    │           ├── FipeApiClient.java

    │           ├── FipeItem.java

    │           ├── NomeDocumentFilter.java

    │           ├── PlacaDocumentFilter.java

    │           ├── TelefoneDocumentFilter.java

    │           ├── UIDesigner.java

    │           └── WrapLayout.java

    │

    └── resources

        └── icons                           # Ícones da aplicação

            ├── add.png

            ├── car.png

            ├── check.png

            ├── customer.png

            ├── dashboard.png

            ├── delete.png

            ├── rental.png

            ├── save.png

            ├── user.png

            └── vehicle.png



### 📁 target
Arquivos gerados após o build com Maven. No momento em que buildar o projeto com o maven, aparecerão os demais arquivos.

├── AutoFacilApp.jar

└── (outros arquivos compilados)



- **`core`**: Contém as classes que representam as entidades do sistema: `Veiculo`, `Cliente` e `Aluguel`.
- **`data`**: Responsável pela persistência dos dados. O `JsonDataManager` é a classe central que salva e carrega os dados nos arquivos `.json` localizados na pasta `registros`.
- **`gui`**: Todas as classes relacionadas à interface gráfica Swing. A `JanelaPrincipal` organiza os painéis de gerenciamento (`PainelGerenciamentoVeiculos`, etc.).
- **`util`**: Um conjunto de ferramentas que auxiliam a aplicação, incluindo o `FipeApiClient` para chamadas de API, o `UIDesigner` para a aparência e os validadores de entrada como `CpfValidator`.
- **`registros`**: Diretório onde os dados da aplicação (veículos, clientes e aluguéis) são armazenados em formato JSON.

## 🚀 Como Executar
Para compilar e executar o projeto, você precisará ter o **JDK 21** e o **Maven** instalados.

1.  **Navegue até o diretório do projeto:**
    ```bash
    cd autofacil
    ```

2.  **Compile o projeto e crie o JAR executável:**
    O Maven irá baixar todas as dependências e criar um arquivo "Fat JAR" (um único arquivo com todo o código e bibliotecas) na pasta `target/`.
    ```bash
    mvn clean package
    ```

3.  **Execute a aplicação:**
    ```bash
    java -jar target/AutoFacilApp.jar
    ```
    *(O nome `AutoFacilApp.jar` é definido no arquivo `pom.xml`)*.

## ✅ Boas Práticas Implementadas
- **Arquitetura em Camadas**: O código é organizado em pacotes com responsabilidades bem definidas (`core`, `data`, `gui`, `util`), seguindo um padrão semelhante ao MVC.
- **Centralização de Estilo**: A classe `UIDesigner` centraliza todas as definições de cores, fontes e bordas, facilitando a manutenção da identidade visual do sistema.
- **Validação de Entrada**: O sistema utiliza `DocumentFilter`s para validar e formatar dados de entrada em tempo real (CPF, Placa, Telefone), melhorando a experiência do usuário e a integridade dos dados.
- **Segurança de Thread em Swing**: A interface gráfica é inicializada de forma segura na *Event Dispatch Thread* (EDT) através do `SwingUtilities.invokeLater`.
- **Gerenciamento de Dependências**: O uso do Maven com o `pom.xml` torna o gerenciamento de bibliotecas externas simples e declarativo.
- **Imutabilidade**: Campos que não devem ser alterados após a criação de um objeto (como `placa` em `Veiculo` e `cpf` em `Cliente`) são declarados como `final`, uma boa prática de programação segura.

## 👨‍💻 Autores

| Nome | Curso | Semestre | Cadeira | Professor |
| :--- | :--- | :--- |:--- |:--- |
| **Mateus Carniel Brambilla e Gustavo Schneider Garcia** | Engenharia de Software | 1º | LABORATÓRIO DE LÓGICA DE PROGRAMAÇÃO | LUIS ANTONIO SCHNEIDERS |

