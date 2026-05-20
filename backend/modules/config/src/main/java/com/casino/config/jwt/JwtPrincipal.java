package com.casino.config.jwt;

import java.util.Set;

public record JwtPrincipal(long userId, Set<String> roles) {}
