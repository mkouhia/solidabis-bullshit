# solidabis-bullshit

[![Heroku](https://heroku-badge.herokuapp.com/?app=obscure-springs-32806)][Heroku]

Participation in [Solidabis code challenge 2019](https://koodihaaste.solidabis.com)

Get a list of Caesar-ciphered strings, and determine whether they are Finnish language.
The cipher employs characters a-z, å, ä, ö; extra characters are left as-is.
Some strings may contain only a bullshit message, thus the program must determine if the
strings are truly Finnish or not.

The implementation compares each string and its deciphered variations against expected
character distribution typical to Finnish language. The probability on being Finnish
is calculated from Pearson's Chi-squared test result.

The program is deployed to [Heroku]. In the web UI, candidate strings are displayed divided
to *bullshit* and *no bullshit* sentences. *No bullshit* sentences are shown in Finnish,
*bullshit* sentences are displayed in their original form.


## Testing commits and deploying

- Always run tests before pushing with `./gradle test` and/or test build process
 with `./gradle clean build`
    - **JDK 12 is required**. See configuration from e.g. [this Stack Overflow answer](https://stackoverflow.com/a/21212790)
    - For Heroku, this is already specified in [system.properties](system.properties)
- Test Heroku with `./gradle stage` and `heroku local`
- Increment version in [build.gradle.kts](build.gradle.kts)
- Git commit
- Push to GitHub master branch; Heroku automatic deployment takes care of the rest
    - Alternatively, push directly to heroku: `git push heroku master`


[Heroku]: https://obscure-springs-32806.herokuapp.com
