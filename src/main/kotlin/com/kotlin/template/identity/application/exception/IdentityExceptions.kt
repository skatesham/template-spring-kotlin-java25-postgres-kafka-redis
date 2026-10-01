package com.kotlin.template.identity.application.exception

class EmailAlreadyRegistered : RuntimeException("Email já cadastrado.")
class InvalidCredentials : RuntimeException("Email ou senha inválidos.")
class UserNotFound : RuntimeException("Usuário não encontrado.")
