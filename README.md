# Padel Score

App per tenere il punteggio durante una partita di padel.

## Componenti

- **android/**: App Android (Kotlin + Jetpack Compose)
- **tizen/**: App Tizen per Samsung Gear S3

## Setup Android

1. Scarica il Samsung Accessory SDK da https://developer.samsung.com/galaxy-watch/develop/samsung-accessory-sdk.html
2. Copia `samsung-accessory-v2.jar` nella cartella `android/app/libs/`
3. Apri `android/` in Android Studio
4. Build & Run su dispositivo Android

## Setup Gear S3

1. Installa Tizen Studio con Wearable SDK
2. Apri la cartella `tizen/` come progetto Tizen Web
3. Firma e installa su Gear S3 via USB o Wireless

## Utilizzo

### Orologio (Gear S3)
- **1 tap** → punto per noi
- **2 tap** → punto agli avversari
- **3 tap** → annulla ultimo punto

### Telefono
- Mostra il punteggio in tempo reale
- Bottoni manuali come backup (se l'orologio non è connesso)

## Punteggio Padel
- Punti: 0 / 15 / 30 / 40 / Vantaggio
- Game: vince chi arriva a 6 con 2 di scarto
- Tiebreak a 6-6 (primo a 7 con 2 di scarto)
- Match: al meglio dei 3 set
