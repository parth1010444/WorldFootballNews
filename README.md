# World Football News

Native Android app that retrieves global football/soccer news from ESPN's public API and displays the results in a `RecyclerView`.

## Setup

1. Open the project in Android Studio.
2. Sync Gradle and run the app.

No API key is required.

## ESPN API Source

The app uses ESPN's global soccer news endpoint:

```text
https://now.core.api.espn.com/v1/sports/news?sport=soccer&limit=50
```

ESPN also supports league-specific soccer news through:

```text
https://site.api.espn.com/apis/site/v2/sports/soccer/{league}/news
```

Examples of league slugs from the ESPN documentation include `eng.1`, `esp.1`, `ger.1`, `ita.1`, `fra.1`, `usa.1`, `uefa.champions`, and `fifa.world`.
