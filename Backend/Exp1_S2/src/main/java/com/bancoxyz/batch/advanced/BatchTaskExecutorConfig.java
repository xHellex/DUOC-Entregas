package com.bancoxyz.batch.advanced;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuracion del TaskExecutor que habilita el procesamiento paralelo.
 *
 * Segun el requisito de la actividad, se configuran 3 hilos de ejecucion
 * paralela. Los parametros de optimizacion de recursos evitan que el
 * proceso batch sature la memoria o la CPU del sistema:
 *   - corePoolSize = 3: siempre hay 3 hilos disponibles, evitando el costo
 *     de crear y destruir hilos repetidamente.
 *   - maxPoolSize = 3: se limita el maximo a 3 para no exceder lo pedido.
 *   - queueCapacity = 25: las tareas en espera se encolan sin crecer sin
 *     control, limitando el uso de memoria.
 */
@Configuration
public class BatchTaskExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Batch-Thread-");
        executor.initialize();
        return executor;
    }
}
