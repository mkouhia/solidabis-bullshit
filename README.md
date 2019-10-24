# solidabis-bullshit

[![Heroku](https://heroku-badge.herokuapp.com/?app=obscure-springs-32806)][Heroku]

Participation in [Solidabis code challenge 2019](https://koodihaaste.solidabis.com)

## Testing commits and deploying

- Always run tests before pushing with `./gradle test` and/or test build process
 with `./gradle clean build`
    - **JDK 12 is required**. See configuration from e.g. [this Stack Overflow answer](https://stackoverflow.com/a/21212790)
    - For Heroku, this is already specified in [system.properties]
- Test Heroku with `heroku local`
- Increment version in [build.gradle.kts]
- Git commit
- Push to GitHub master branch; Heroku automatic deployment takes care of the rest
    - Alternatively, push directly to heroku: `git push heroku master`


[Heroku]: https://obscure-springs-32806.herokuapp.com
