package com.bancoxyz.batch.advanced;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuracion del TaskExecutor que ejecuta las particiones en paralelo.
 *
 * El tamano del pool se alinea con el numero de particiones (gridSize) para
 * que todas puedan ejecutarse simultaneamente. Los parametros son
 * configurables desde application.properties, lo que permite comparar
 * distintas configuraciones (numero de hilos y particiones) y encontrar la
 * optima, tal como pide la actividad.
 *
 *   corePoolSize / maxPoolSize = gridSize  -> un hilo por particion
 *   queueCapacity acotado                  -> evita crecimiento de memoria
 */
@Configuration
public class BatchTaskExecutorConfig {

    @Value("${app.grid.size}")
    private int gridSize;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(gridSize);
        executor.setMaxPoolSize(gridSize);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Batch-Part-");
        executor.initialize();
        return executor;
    }
}
