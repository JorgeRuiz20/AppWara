package com.example.wara.domain

import com.example.wara.core.result.Resource
import com.example.wara.domain.model.AuthSession
import com.example.wara.domain.model.PaginatedResult
import com.example.wara.domain.model.Trabajador
import com.example.wara.domain.model.Usuario
import com.example.wara.domain.repository.AuthRepository
import com.example.wara.domain.repository.TrabajadorRepository
import com.example.wara.domain.usecase.auth.LoginUseCase
import com.example.wara.domain.usecase.auth.RegisterUseCase
import com.example.wara.domain.usecase.trabajador.CrearTrabajadorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseValidationTest {

    // Mock AuthRepository
    private val fakeAuthRepository = object : AuthRepository {
        override suspend fun login(nombreUsuario: String, password: String): Resource<AuthSession> =
            Resource.Success(AuthSession("dummy_token", nombreUsuario))

        override suspend fun register(nombreUsuario: String, password: String): Resource<String> =
            Resource.Success("Usuario registrado exitosamente.")

        override suspend fun getUsuarioActual(): Resource<Usuario> =
            Resource.Success(Usuario(1, "jorge"))

        override suspend fun logout() {}
        override fun getSavedToken(): Flow<String?> = flowOf("dummy_token")
        override fun getSavedUsername(): Flow<String?> = flowOf("jorge")
    }

    // Mock TrabajadorRepository
    private val fakeTrabajadorRepository = object : TrabajadorRepository {
        override suspend fun listarTrabajadores(
            dni: String?,
            pagina: Int,
            tamanoPagina: Int,
            soloActivos: Boolean,
            forceRefresh: Boolean
        ): Resource<PaginatedResult<Trabajador>> =
            Resource.Success(PaginatedResult(emptyList(), 1, 10, 0, 0))

        override suspend fun obtenerPorId(id: Int): Resource<Trabajador> =
            Resource.Success(Trabajador(1, "Rosa", "Castillo", "76789012", 52))

        override suspend fun agregarTrabajador(
            nombre: String,
            apellido: String,
            dni: String,
            edad: Int
        ): Resource<Trabajador> =
            Resource.Success(Trabajador(1, nombre, apellido, dni, edad))

        override suspend fun actualizarTrabajador(
            id: Int,
            nombre: String,
            apellido: String,
            edad: Int
        ): Resource<Trabajador> =
            Resource.Success(Trabajador(id, nombre, apellido, "76789012", edad))

        override suspend fun eliminarTrabajador(id: Int): Resource<Unit> =
            Resource.Success(Unit)
    }

    @Test
    fun `LoginUseCase fails on empty username`() = runBlocking {
        val useCase = LoginUseCase(fakeAuthRepository)
        val result = useCase("", "password123")
        assertTrue(result is Resource.Error)
        assertEquals("El usuario es obligatorio.", (result as Resource.Error).message)
    }

    @Test
    fun `LoginUseCase fails on empty password`() = runBlocking {
        val useCase = LoginUseCase(fakeAuthRepository)
        val result = useCase("jorge", "")
        assertTrue(result is Resource.Error)
        assertEquals("La contraseña es obligatoria.", (result as Resource.Error).message)
    }

    @Test
    fun `RegisterUseCase validates username length and password length`() = runBlocking {
        val useCase = RegisterUseCase(fakeAuthRepository)

        // Too short username
        val r1 = useCase("ab", "password123", "password123")
        assertTrue(r1 is Resource.Error)
        assertEquals("El nombre de usuario debe tener entre 3 y 50 caracteres.", (r1 as Resource.Error).message)

        // Too short password
        val r2 = useCase("jorge", "short", "short")
        assertTrue(r2 is Resource.Error)
        assertEquals("La contraseña debe tener al menos 8 caracteres.", (r2 as Resource.Error).message)

        // Mismatched passwords
        val r3 = useCase("jorge", "password123", "different123")
        assertTrue(r3 is Resource.Error)
        assertEquals("Las contraseñas no coinciden.", (r3 as Resource.Error).message)

        // Valid
        val r4 = useCase("jorge", "password123", "password123")
        assertTrue(r4 is Resource.Success)
    }

    @Test
    fun `CrearTrabajadorUseCase validates dni and age`() = runBlocking {
        val useCase = CrearTrabajadorUseCase(fakeTrabajadorRepository)

        // Invalid DNI: not 8 digits
        val r1 = useCase("Rosa", "Castillo", "12345", 30)
        assertTrue(r1 is Resource.Error)
        assertEquals("El DNI debe tener exactamente 8 dígitos numéricos.", (r1 as Resource.Error).message)

        // Invalid age: under 18
        val r2 = useCase("Rosa", "Castillo", "12345678", 17)
        assertTrue(r2 is Resource.Error)
        assertEquals("La edad debe estar entre 18 y 80 años.", (r2 as Resource.Error).message)

        // Valid worker
        val r3 = useCase("Rosa", "Castillo", "76789012", 52)
        assertTrue(r3 is Resource.Success)
    }
}
