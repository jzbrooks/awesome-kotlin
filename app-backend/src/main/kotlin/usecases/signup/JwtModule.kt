package usecases.signup

import infra.config.ConfigModule
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import infra.config.decode
import io.heapy.komok.tech.di.lib.Module
import kotlinx.serialization.Serializable
import java.util.*

@Module
open class JwtModule(
    private val configModule: ConfigModule,
) {
    open val jwtConfig: JwtConfig by lazy {
        configModule.decode("jwt", JwtConfig.serializer())
    }

    open val generateJwt by lazy {
        GenerateJwt(
            jwtConfig = jwtConfig,
        )
    }

    @Serializable
    data class JwtConfig(
        val audience: String,
        val realm: String,
        val issuer: String,
        val secret: String,
    )
}

class GenerateJwt(
    private val jwtConfig: JwtModule.JwtConfig,
) {
    operator fun invoke(
        id: String,
    ): String {
        return JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withClaim("id", id)
            .withIssuedAt(Date(System.currentTimeMillis()))
            .sign(Algorithm.HMAC512(jwtConfig.secret))
    }
}
