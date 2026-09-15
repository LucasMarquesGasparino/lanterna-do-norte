# Lanterna do Norte

Aplicador de um tema original de fantasia invernal para Android. Cria
localmente um papel de parede com céu noturno, pinheiros, neve e lanterna
dourada — sem baixar imagens nem enviar dados.

- Pacote: `com.lanternadonorte`

## Uso

1. Instale `build/Lanterna-do-Norte.apk`.
2. Abra **Lanterna do Norte** e toque em **Aplicar papel de parede**.
3. Opcional: **Usar como tela inicial** e selecione o app nas configurações.
   Isso mantém seus apps, exibidos numa grade temática ("O Portal do Norte").

O launcher padrão da Motorola não dá permissão a apps externos para trocar os
ícones globais — a troca de ícones existe só no modo de tela inicial; o papel
de parede funciona com qualquer launcher.

## Privacidade e permissões

Sem internet, mídia, contatos, localização ou notificações. Só
`SET_WALLPAPER` (tela inicial + bloqueio).

## Compilar

Pipeline padrão aapt2+javac+d8 (ver pastas irmãs). Arte 100% procedural em
`src/`.

## Estrutura

```
lanterna-do-norte/
├── src/com/lanternadonorte/MainActivity.java  # arte + launcher alternativo
├── res/  # ícone e estilo
└── build/Lanterna-do-Norte.apk
```
