# Lanterna do Norte

Aplicador de um tema original de fantasia invernal para Android. Ele cria localmente
um papel de parede com céu noturno, pinheiros, neve e uma lanterna dourada, sem baixar
imagens ou enviar dados.

## Uso

1. Instale `build/Lanterna-do-Norte.apk`.
2. Abra **Lanterna do Norte** e toque em **Aplicar papel de parede**.
3. Opcionalmente, toque em **Usar como tela inicial** e selecione o aplicativo nas
   configurações do Android. Isso mantém seus apps e os exibe em uma grade temática.

O launcher padrão da Motorola não dá permissão a apps externos para substituir os
ícones globais. Por isso, a alteração de ícones existe somente no modo de tela inicial
opcional; o papel de parede funciona com o launcher que você já usa.

## Privacidade e permissões

O APK não declara acesso à internet, mídia, contatos, localização ou notificações.
Ele usa somente `SET_WALLPAPER` para gravar a arte em tela inicial e bloqueio.

## Estrutura

- `src/` — código Java da interface, da tela inicial alternativa e da arte procedural.
- `res/` — ícone e estilo do app.
- `build/Lanterna-do-Norte.apk` — APK assinado gerado para instalação.
