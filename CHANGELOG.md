# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Unreleased]

### Planejado

- Sistema de níveis com progressão
- Implementação de portas lógicas (AND, OR, NOT, XOR, XNOR, NAND, NOR)
- Sistema de pontuação por estrelas
- Menu principal e navegação
- Sistema de entrada de bits (0s e 1s)
- Feedback visual
- Portabilidade para desktop e web

## [0.1.0] - 2025-11-04

### Adicionado

- Clone inicial de exemplo com libGDX 1.13.0 e Gradle 8.10
- README.md com especificação do projeto
- CHANGELOG.md para controle de versões

### Documentação

- Proposta do jogo "Bit it!"
- Mecânicas de jogo planejadas
- Especificação de tecnologias utilizadas


## [0.2.0] - 2025-11-11

### Adicionado

- Criação de todas as classes lógicas básicas do jogo
- Implementação inicial para teste de portas lógicas e evaluação de expressões
- Criação de texturas e do esqueleto inicial do jogo
- Renderização básica do circuito com as texturas criadas


## [0.3.0] - 2025-11-12

### Adicionado

- Criação de um parser simples para interpretar expressões lógicas expressas em texto para JSON
- Criação de níveis do jogo no arquivo ``levels.txt ``

### Modificado

- Refatoração do sistema de renderização de todo o circuito
- Novo modelo de criação de entradas e saídas das portas lógicas
- Configuração do Gradle para adicionar task de conversão de níveis
- Recompilação do GWT para build HTML funcionar corretamente

### Documentação

- Atualização do CHANGELOG.md com novas versões e mudanças realizadas
- Atualização no instructions.md com instruções para construção dos níveis


## [0.4.0] - 2025-11-13

### Adicionado

- Criação do parser de JSON para circuito lógico do nível
- Novos sprites do jogo na pasta ``assets/textures/UI``

### Modificado

- Refatoração do sistema de renderização dos fios para obedecer a nova orientação (fios horizontais e verticais)
- Refatoração de métodos das classes Circuit e CircuitBuilder

### Documentação

- Atualização do CHANGELOG.md com as mudanças realizadas

## [0.5.0] - 2025-11-15

### Adicionado

- Tela inicial, menus e tela de jogo

### Modificado

- Refatoração na geração do circuito na tela, tanto com o reposicionamento das portas lógicas quanto a criação de fios
- Refatoração de classes como LevelScreen e GameScreen para facilitar a compreensão

### Corrigido

- Crashes aleatórios e opção para jogar o mesmo jogo mais de uma vez

### Documentação

- Atualização do CHANGELOG.md com as mudanças realizadas


## [0.6.0] - 2025-11-23

### Adicionado

- Menus de pause e de nível concluído

### Modificado

- Refatoração na tela de jogo (GameScreen) para adicionar verificação de vitória e cálculo de estrelas
- Refatoração do LevelManager para atualizar o progresso do nível corretamente
- Refatoração do LevelToJSON para adicionar versão standalone do conversor de níveis

### Corrigido

- Correções mínimas em bugs

### Documentação

- Atualização do CHANGELOG.md com as mudanças realizadas
  

## [1.0.0] - 2025-11-26

Realease inicial do jogo "Bit it!" com todas as funcionalidades planejadas implementadas.

### Adicionado

- Menu de créditos e de como jogar
- Menu de níveis com pontuação por estrelas aparente

### Modificado

- Refatoração para adicionar novos níveis e balancear a dificuldade
- Modificação na tela inicial do jogo
- Modificação no sistema de renderização dos fios para melhorar a visualização

### Corrigido

- Correções mínimas em bugs pontuais

### Documentação

- Finalização do README.md
- Atualização do CHANGELOG.md com as mudanças realizadas
- Criação do Diagrama de Classes UML

---

<!--
- **Adicionado** - para novas funcionalidades
- **Modificado** - para mudanças em funcionalidades existentes
- **Descontinuado** - para funcionalidades que serão removidas
- **Removido** - para funcionalidades removidas
- **Corrigido** - para correção de bugs
- **Segurança** - para vulnerabilidades corrigidas
- **Documentação** - para mudanças na documentação

guia de versionamento semântico:
    0 . 1 . 0 
    │   │   └── PATCH (correções/bugfixes)
    │   └────── MINOR (novas funcionalidades)
    └────────── MAJOR (mudanças incompatíveis)

links:
- [Unreleased]: compara última tag com HEAD (branch atual)
- [X.Y.Z]: link para a release/tag ou comparação entre versões
- Criar tag: git tag v0.1.0 && git push origin v0.1.0
- Depois criar Release no GitHub com essa tag
-->

[Unreleased]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v0.1.0
[0.2.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v0.2.0
[0.3.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v0.3.0
[0.4.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v0.4.0
[0.5.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v0.5.0
[0.6.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v0.6.0
[1.0.0]: https://github.com/elc117/gamification-2025b-samuel-tiago-steffler/releases/tag/v1.0.0
