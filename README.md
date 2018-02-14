# dg.fy

## How does this work?
* Backend with API to manage a playlist (includes searching and voting of tracks)
* React webapp which connects to the API
* Android app with spotify library which connects to the API and pulls songs from it

## How to run
1. Get a Spotify ClientId and Secret by creating an App here: https://beta.developer.spotify.com/dashboard/applications
2. Build the frontend using gradle: './gradlew buildWebapp'
3. Run server from IDE or run the jar manually after building with './gradlew server:jar'
   * java -jar server.jar <clientId> <clientSecret> <admin-password>
4. (Optional) Run android app from IDE or install to connected device with './gradlew app:installDebug'
   * A spotify premium account is required to play songs
