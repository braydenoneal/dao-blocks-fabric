package com.braydenoneal.dao.util;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CompletableFutureStream {
    public static CompletableFuture<?> of(Stream<CompletableFuture<?>> completableFutureStream) {
        return CompletableFuture.allOf(completableFutureStream.toArray(CompletableFuture[]::new));
    }
}
