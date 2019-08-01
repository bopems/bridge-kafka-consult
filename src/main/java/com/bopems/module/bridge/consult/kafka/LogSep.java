package com.bopems.module.bridge.consult.kafka;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
public class LogSep implements Log, Serializable {


    // ------------------------------------------------------------- Attributes

    /** The fully qualified name of the Log4JLogger class. */
    private static final String FQCN = Log4JLogger.class.getName();

    private static final boolean is12 = Priority.class.isAssignableFrom(Level.class);

    /** Log to this logger */
    private static Logger logger = null;

    /** Logger name */
    private String name = null;

    ///
    /// Variaveis de ambiente
    ///

    private static String DISCOVERY_HOST;
    private static String DISCOVERY_PORT;
    private static String KAFKA_GRAYLOG_HOST;
    private static String KAFKA_GRAYLOG_PORT;
    private static String KAFKA_SERVICE_GRAYLOG_NAME;
    private static String APP_PROJECT;
    private static String APP_MODULE;
    private static String APP_SERVICE;
    private static String APP_NAME;

    private static KafkaTemplate<String, String> kafkaTemplate;
    private static String TOPIC_GRAYLOG;

    public String getKafkaHost(String body) {
        return body.substring(body.indexOf("ip:"), body.indexOf("ip:") + body.substring(body.indexOf("ip:")).indexOf("]")).toString().replace("\"","").split(":")[1].trim();
    }
    public String getKafkaPort(String body) {
        return body.substring(body.indexOf("ServicePort"), body.indexOf("ServicePort") + body.substring(body.indexOf("ServicePort")).indexOf("}")).replace("\"","").split(":")[1].trim();
    }

    // ------------------------------------------------------------ Constructor

    public LogSep() {
    }

    /**
     * Base constructor.
     */
    public LogSep(String name) {
        this.name = name;
        this.logger = getLogger();
    }

    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /** For use with a log4j factory.
     */
    public LogSep(Logger logger) {
        this.name = logger.getName();
        this.logger = logger;
    }

    private Map<String, Object> producerConfigs() {

        String uri = new StringBuilder().append(this.KAFKA_GRAYLOG_HOST).append(":").append(this.KAFKA_GRAYLOG_PORT).toString();

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, uri);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }

    // --------------------------------------------------------- Implementation


    /**
     * Log a message to the Log4j Logger with <code>TRACE</code> priority.
     * Currently logs to <code>DEBUG</code> level in Log4J.
     */
    public void trace(Object message) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, null );
        } else {
            getLogger().log(FQCN, Level.DEBUG, message, null );
        }
        this.GraylogSend("DEBUG-", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>TRACE</code> priority.
     * Currently logs to <code>DEBUG</code> level in Log4J.
     */
    public void trace(Object message, Throwable t) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, t );
        } else {
            getLogger().log(FQCN, Level.DEBUG, message, t );
        }
        this.GraylogSend("TRACE-", message);
    }

    /**
     * Log a message to the Log4j Logger with <code>TRANSACIONAL</code> priority.
     */
    public void transacional(Object message) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.INFO, message, null );
        } else {
            getLogger().log(FQCN, Level.INFO, message, null );
        }
        this.GraylogSend("TRANSACIONAL-", message);
    }

    /**
     * Log a message to the Log4j Logger with <code>DEBUG</code> priority.
     */
    public void debug(Object message) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, null );
        } else {
            getLogger().log(FQCN, Level.DEBUG, message, null );
        }
        this.GraylogSend("DEBUG-", message);
    }

    /**
     * Log an error to the Log4j Logger with <code>DEBUG</code> priority.
     */
    public void debug(Object message, Throwable t) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.DEBUG, message, t );
        } else {
            getLogger().log(FQCN, Level.DEBUG, message, t );
        }
        this.GraylogSend("DEBUG-", message);
    }


    /**
     * Log a message to the Log4j Logger with <code>INFO</code> priority.
     */
    public void info(Object message) {

        if(is12) {
            getLogger().log(FQCN, (Priority) Level.INFO, message, null );
        } else {
            getLogger().log(FQCN, Level.INFO, message, null );
        }
        this.GraylogSend("INFO-", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>INFO</code> priority.
     */
    public void info(Object message, Throwable t) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.INFO, message, t );
        } else {
            getLogger().log(FQCN, Level.INFO, message, t );
        }
        this.GraylogSend("INFO-", message);
    }

    public void GraylogSend(String level, Object obj) {

        Gson gson = new Gson();
        String message = gson.toJson(obj);

        Message<String> kafkaMessage = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, new StringBuilder(level).append(this.TOPIC_GRAYLOG).toString())
                .build();

        kafkaTemplate.send(kafkaMessage);
    }


    /**
     * Log a message to the Log4j Logger with <code>WARN</code> priority.
     */
    public void warn(Object message) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.WARN, message, null );
        } else {
            getLogger().log(FQCN, Level.WARN, message, null );
        }
        this.GraylogSend("WARN-", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>WARN</code> priority.
     */
    public void warn(Object message, Throwable t) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.WARN, message, t );
        } else {
            getLogger().log(FQCN, Level.WARN, message, t );
        }
        this.GraylogSend("WARN-", message);
    }


    /**
     * Log a message to the Log4j Logger with <code>ERROR</code> priority.
     */
    public void error(Object message) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.ERROR, message, null );
        } else {
            getLogger().log(FQCN, Level.ERROR, message, null );
        }
        this.GraylogSend("ERROR-", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>ERROR</code> priority.
     */
    public void error(Object message, Throwable t) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.ERROR, message, t );
        } else {
            getLogger().log(FQCN, Level.ERROR, message, t );
        }
        this.GraylogSend("ERROR-", message);
    }


    /**
     * Log a message to the Log4j Logger with <code>FATAL</code> priority.
     */
    public void fatal(Object message) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.FATAL, message, null );
        } else {
            getLogger().log(FQCN, Level.FATAL, message, null );
        }
        this.GraylogSend("FATAL-", message);
    }


    /**
     * Log an error to the Log4j Logger with <code>FATAL</code> priority.
     */
    public void fatal(Object message, Throwable t) {
        if(is12) {
            getLogger().log(FQCN, (Priority) Level.FATAL, message, t );
        } else {
            getLogger().log(FQCN, Level.FATAL, message, t );
        }
        this.GraylogSend("FATAL-", message);
    }


    /**
     * Return the native Logger instance we are using.
     */
    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(name);
            this.config();
        }

        return (this.logger);
    }

    private void config() {

        String message;

        this.DISCOVERY_HOST = System.getenv("spring.cloud.consul.host");
        this.DISCOVERY_PORT = System.getenv("spring.cloud.consul.port");
        this.KAFKA_GRAYLOG_HOST = System.getenv("KAFKA_GRAYLOG_HOST");
        this.KAFKA_GRAYLOG_PORT = System.getenv("KAFKA_GRAYLOG_PORT");
        this.APP_PROJECT = System.getenv("app.project");
        this.APP_MODULE = System.getenv("app.module");
        this.APP_SERVICE = System.getenv("app.service");
        this.APP_NAME = System.getenv("app.name");

        if (APP_PROJECT == "" || APP_PROJECT == null ||
            APP_MODULE == "" || APP_MODULE == null ||
            APP_SERVICE == "" || APP_NAME == null ||
            APP_NAME == "" || APP_NAME == null){

            message = "Tópico do LOG não pode ser montado, defina as variáveis de ambiente 'app.project', 'app.module', 'app.service' e 'app.name' para inicialização.";
            getLogger().log(FQCN, Level.FATAL, message, null );
        }
        else {
            this.TOPIC_GRAYLOG = new StringBuilder()
                    .append(APP_PROJECT).append("-")
                    .append(APP_MODULE).append("-")
                    .append(APP_SERVICE).append("-")
                    .append(APP_NAME).toString();
        }

        if (DISCOVERY_HOST == "" || DISCOVERY_HOST == null ||
            DISCOVERY_PORT == "" || DISCOVERY_PORT == null) {

            message = "Consul não pode ser descoberto, defina as variáveis de ambiente 'spring.cloud.consul.host' e 'spring.cloud.consul.port' para inicialização.";
            getLogger().log(FQCN, Level.FATAL, message, null );
        }

        if (KAFKA_GRAYLOG_HOST == "" || KAFKA_GRAYLOG_HOST == null ||
            KAFKA_GRAYLOG_PORT == "" || KAFKA_GRAYLOG_PORT == null)
        {
            message = "Variavel de ambiente 'KAFKA_HOST' e 'KAFKA_PORT' nao definidos.";
            getLogger().log(FQCN, Level.WARN, message, null );

            KAFKA_SERVICE_GRAYLOG_NAME = System.getenv("KAFKA_SERVICE_GRAYLOG_NAME");
            if (KAFKA_SERVICE_GRAYLOG_NAME == "" || KAFKA_SERVICE_GRAYLOG_NAME == null) {

                message = "Variavel de ambiente 'KAFKA_SERVICE_GRAYLOG_NAME' não definida, usando valor default.";
                getLogger().log(FQCN, Level.WARN, message, null );

                KAFKA_SERVICE_GRAYLOG_NAME = "kafka-graylog";
            }

            message = new StringBuilder("'KAFKA_SERVICE_GRAYLOG_NAME = ").append(KAFKA_SERVICE_GRAYLOG_NAME).append("'.").toString();
            getLogger().log(FQCN, Level.INFO, message, null );

            message = "Buscando serviço no Service Discovery...";
            getLogger().log(FQCN, Level.WARN, message, null );

            try {
                URL obj = null;
                String url = new StringBuilder("http://")
                        .append(DISCOVERY_HOST).append(":")
                        .append(DISCOVERY_PORT)
                        .append("/v1/catalog/service/")
                        .append(KAFKA_SERVICE_GRAYLOG_NAME)
                        .toString();

                getLogger().log(FQCN, Level.INFO, "Sending 'GET' request to URL : " + url, null );

                obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection)obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();

                getLogger().log(FQCN, Level.INFO, "Response Code: " + responseCode, null );

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
                in.close();
                getLogger().log(FQCN, Level.INFO, response.toString(), null );

                ///
                /// Busca pelo IP do servico contigo em ServiceTags.
                //
                //  [
                //    {
                //      "Node": "consul",
                //      "Address": "172.18.0.5",
                //      "ServiceID": "a1770dbca80e:kafka:9092",
                //      "ServiceName": "kafka",
                //      "ServiceTags": [
                //        "ip:10.173.200.40"
                //      ],
                //      "ServiceAddress": "172.17.0.1",
                //      "ServicePort": 9092}
                //  ]
                ///

                this.KAFKA_GRAYLOG_HOST = this.getKafkaHost(response.toString());
                this.KAFKA_GRAYLOG_PORT = this.getKafkaPort(response.toString());

                message = new StringBuilder("'KAFKA_GRAYLOG_HOST: ").append(KAFKA_GRAYLOG_HOST).append("', 'KAFKA_GRAYLOG_PORT: ").append(KAFKA_GRAYLOG_PORT).append("'.").toString();
                getLogger().log(FQCN, Level.INFO, message, null );

            } catch (MalformedURLException e) {
                getLogger().log(FQCN, Level.TRACE, e.getMessage(), e.fillInStackTrace());
            } catch (ProtocolException e) {
                getLogger().log(FQCN, Level.TRACE, e.getMessage(), e.fillInStackTrace());
            } catch (IOException e) {
                getLogger().log(FQCN, Level.TRACE, e.getMessage(), e.fillInStackTrace());
            }
        }
        else {
            message = new StringBuilder("'KAFKA_GRAYLOG_HOST= ").append(KAFKA_GRAYLOG_HOST).append("', 'KAFKA_GRAYLOG_PORT=").append(KAFKA_GRAYLOG_PORT).append("'.").toString();

            getLogger().log(FQCN, Level.INFO, message, null );
            getLogger().log(FQCN, Level.INFO, "Variáveis carregadas.", null );
        }

        kafkaTemplate = kafkaTemplate();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>DEBUG</code> priority.
     */
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>ERROR</code> priority.
     */
    public boolean isErrorEnabled() {
        if(is12) {
            return getLogger().isEnabledFor((Priority) Level.ERROR);
        } else {
            return getLogger().isEnabledFor(Level.ERROR);
        }
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>FATAL</code> priority.
     */
    public boolean isFatalEnabled() {
        if(is12) {
            return getLogger().isEnabledFor((Priority) Level.FATAL);
        } else {
            return getLogger().isEnabledFor(Level.FATAL);
        }
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>INFO</code> priority.
     */
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>TRACE</code> priority.
     * For Log4J, this returns the value of <code>isDebugEnabled()</code>
     */
    public boolean isTraceEnabled() {
        return getLogger().isDebugEnabled();
    }

    /**
     * Check whether the Log4j Logger used is enabled for <code>WARN</code> priority.
     */
    public boolean isWarnEnabled() {
        if(is12) {
            return getLogger().isEnabledFor((Priority) Level.WARN);
        } else {
            return getLogger().isEnabledFor(Level.WARN);
        }
    }
}
