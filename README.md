# Knowlio
Dive into a world where ideas ignite, problems unravel, and innovation thrives. Knowlio is your bridge to brilliance, combining AI's theoretical insights with the practical genius of experts. 🚀
## Knowlio for Android

[Knowlio] is a hub for ideas and problem-solving techinques, which is fully moderated by Artificial Intelligence.
This repo contains the official source code for [Knowlio App for Android](https://play.google.com/store/apps/details?id=com.orpheum.knowlio).

## Strictly for Educational Purpose

I welcome all coding enthusiasts to use the source code of Knowlio to understand UI/UX, backend, and AI integration through practical approach.

### Compilation Guide

**Note**: This repo contains dummy release.keystore,  google-services.json and filled variables inside BuildVars.java. Before publishing your own APKs please make sure to replace all these files with your own.

You will require Android Studio 3.4, Android NDK rev. 20 and Android SDK 8.1

1. Download the Knowlio source code from this repo.
2. Copy your release.keystore into TMessagesProj/config
3.  Go to https://console.firebase.google.com/, create two android apps with different application IDs, turn on firebase messaging and download google-services.json.
4.  Open the project in the Studio (note that it should be opened, NOT imported).
5. You are ready to compile Knowlio.

