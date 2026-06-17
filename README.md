# Notifications Library

Librería Java 21 para enviar notificaciones por Email, SMS, Push y Slack. Framework-agnostic: sin Spring, sin CDI, configuración 100% Java. Los proveedores están simulados (sin llamadas HTTP reales).

**Requisitos:** Java 21+

---

## Instalación

La librería se distribuye como artefactos Maven locales. Primero compila e instala en tu repositorio local (`~/.m2`).

### Clonar y compilar

```bash
git clone <url-del-repositorio>
cd notifications-lib
./mvnw clean install
```

### Maven — dependencias en tu proyecto

Agrega las dependencias según lo que necesites:

```xml
<dependencies>
    <!-- API pública, dominio, pipeline -->
    <dependency>
        <groupId>com.notifications</groupId>
        <artifactId>notification-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Proveedores simulados (SendGrid, Twilio, FCM, Slack, etc.) -->
    <dependency>
        <groupId>com.notifications</groupId>
        <artifactId>notification-providers</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

> Solo necesitas `notification-core` si implementas tus propios proveedores. Agrega `notification-providers` para usar las integraciones incluidas.

### Gradle — dependencias en tu proyecto

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.notifications:notification-core:1.0.0-SNAPSHOT")
    implementation("com.notifications:notification-providers:1.0.0-SNAPSHOT")
}
```

### Ejecutar el demo del repositorio

**Con Docker (recomendado):**

```bash
docker build -t notifications-lib .
docker run --rm notifications-lib
```

**Localmente:**

```bash
./mvnw -pl notification-examples exec:java
```

---

## Quick Start

Ejemplo mínimo para enviar un email:

```java
import com.notifications.api.NotificationClient;
import com.notifications.config.EmailChannelConfig;
import com.notifications.domain.EmailNotification;
import com.notifications.model.ApiCredential;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;
import com.notifications.providers.email.SendGridProvider;
import com.notifications.result.NotificationResult;

public class App {

    public static void main(String[] args) {
        NotificationClient client = NotificationClient.builder()
                .email(new EmailChannelConfig(
                        new SendGridProvider(
                                new ApiCredential("SG.xxxx", "your-secret"),
                                "noreply@example.com"
                        ),
                        "noreply@example.com"
                ))
                .build();

        NotificationResult result = client.send(new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello from Notifications Library!"),
                "Welcome"
        ));

        System.out.println(result);
        // NotificationResult[..., status=SUCCESS, channel=EMAIL, providerName=sendgrid, ...]
    }
}
```

### Manejo de errores

```java
import com.notifications.exception.ConfigurationException;
import com.notifications.exception.ProviderException;
import com.notifications.exception.ValidationException;

try {
    client.send(notification);
} catch (ValidationException e) {
    // Email inválido, teléfono sin formato E.164, etc.
} catch (ConfigurationException e) {
    // Canal no configurado en el Builder
} catch (ProviderException e) {
    // Error del proveedor durante el envío
}
```

---

## Configuración

Toda la configuración se realiza en Java mediante `NotificationClient.builder()`. No hay archivos YAML ni `application.properties`.

### Builder — opciones disponibles

| Método | Descripción | Requerido |
|---|---|---|
| `.email(EmailChannelConfig)` | Configura canal Email | Al menos uno |
| `.sms(SmsChannelConfig)` | Configura canal SMS | Al menos uno |
| `.push(PushChannelConfig)` | Configura canal Push | Al menos uno |
| `.slack(SlackChannelConfig)` | Configura canal Slack | Al menos uno |
| `.templateEngine(TemplateEngine)` | Motor de templates `{{variable}}` | No (default: sin templates) |
| `.eventPublisher(NotificationEventPublisher)` | Publicador de eventos | No (default: sin eventos) |
| `.executor(Executor)` | Executor para envíos async | No (default: `ForkJoinPool`) |

### Email

```java
import com.notifications.config.EmailChannelConfig;
import com.notifications.providers.email.SendGridProvider;
// o: import com.notifications.providers.email.MailgunProvider;

.email(new EmailChannelConfig(
    new SendGridProvider(
        new ApiCredential("SG.api-key", "api-secret"),  // credenciales del proveedor
        "noreply@example.com"                            // remitente (from)
    ),
    "noreply@example.com"                                // fromAddress del canal
))
```

| Parámetro | Descripción |
|---|---|
| `ApiCredential` | API key y secret del proveedor |
| `fromAddress` (provider) | Dirección remitente usada por el proveedor |
| `fromAddress` (config) | Remitente registrado en la configuración del canal |

**Proveedores disponibles:** `SendGridProvider`, `MailgunProvider`

```java
// Alternativa con Mailgun
new MailgunProvider(new ApiCredential("key-xxx", "secret"), "mg.example.com")
```

### SMS

```java
import com.notifications.config.SmsChannelConfig;
import com.notifications.providers.sms.TwilioProvider;

.sms(new SmsChannelConfig(
    new TwilioProvider(
        new ApiCredential("ACxxxx", "auth-token"),  // Account SID + Auth Token
        "+14155551234"                                 // número remitente Twilio
    ),
    "+14155551234"                                     // fromNumber del canal
))
```

| Parámetro | Descripción |
|---|---|
| `ApiCredential.key` | Twilio Account SID |
| `ApiCredential.secret` | Twilio Auth Token |
| `fromNumber` | Número remitente en formato E.164 (`+` obligatorio) |

**Proveedor disponible:** `TwilioProvider`

> Los destinatarios SMS deben estar en formato E.164 (ej. `+14155552671`). La validación usa `libphonenumber`.

### Push

```java
import com.notifications.config.PushChannelConfig;
import com.notifications.providers.push.FcmProvider;

.push(new PushChannelConfig(
    new FcmProvider(
        new ApiCredential("firebase", "server-key"),  // credenciales FCM
        "my-firebase-project"                           // Firebase project ID
    )
))
```

| Parámetro | Descripción |
|---|---|
| `ApiCredential` | Server key / credenciales de Firebase |
| `projectId` | ID del proyecto Firebase |

**Proveedor disponible:** `FcmProvider`

El destinatario de una push es el **device token** del dispositivo:

```java
new PushNotification(
    NotificationId.generate(),
    new Recipient("device-token-abc123"),
    new MessageBody("You have a new message"),
    "New message"   // título de la notificación
)
```

### Slack

```java
import com.notifications.config.SlackChannelConfig;
import com.notifications.providers.slack.SlackWebhookProvider;

.slack(new SlackChannelConfig(
    new SlackWebhookProvider(
        new ApiCredential("slack", "webhook-token"),
        "https://hooks.slack.com/services/T000/B000/XXXXXXXX"
    ),
    "https://hooks.slack.com/services/T000/B000/XXXXXXXX"
))
```

| Parámetro | Descripción |
|---|---|
| `ApiCredential` | Token del webhook |
| `webhookUrl` | URL completa del Incoming Webhook de Slack |

**Proveedor disponible:** `SlackWebhookProvider`

### Templates (opcional)

```java
import com.notifications.template.SimpleTemplateEngine;

.templateEngine(new SimpleTemplateEngine(Map.of(
    "name", "Ana",
    "code", "123456"
)))
```

Reemplaza `{{name}}` y `{{code}}` en el cuerpo, asunto y título de las notificaciones antes del envío.

### Eventos (opcional)

```java
import com.notifications.event.DefaultEventPublisher;

.eventPublisher(new DefaultEventPublisher(event ->
    System.out.println(event.type() + " -> " + event.message())
))
```

### Configuración completa (todos los canales)

Ver ejemplo completo en:

`notification-examples/src/main/java/com/notifications/examples/NotificationExamples.java`

---

## Proveedores soportados

Todos los proveedores simulan respuestas realistas. No realizan llamadas HTTP externas.

| Canal | Proveedor | Clase | `providerName()` |
|---|---|---|---|
| Email | SendGrid | `SendGridProvider` | `sendgrid` |
| Email | Mailgun | `MailgunProvider` | `mailgun` |
| SMS | Twilio | `TwilioProvider` | `twilio` |
| Push | Firebase Cloud Messaging | `FcmProvider` | `fcm` |
| Slack | Slack Incoming Webhook | `SlackWebhookProvider` | `slack-webhook` |

### Cambiar de proveedor

El pipeline no cambia. Solo reemplazas la instancia en el Builder:

```java
// SendGrid
new SendGridProvider(credential, "noreply@example.com")

// Mailgun (mismo canal EMAIL)
new MailgunProvider(credential, "mg.example.com")
```

### Implementar un proveedor propio

```java
public final class MyEmailProvider implements NotificationProvider {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public String providerName() {
        return "my-provider";
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof EmailNotification;
    }

    @Override
    public NotificationResult send(Notification notification) {
        // tu lógica de envío
    }
}
```

---

## API Reference

### `NotificationClient`

Punto de entrada principal de la librería (Facade).

| Método | Descripción |
|---|---|
| `static Builder builder()` | Crea el builder de configuración |
| `NotificationResult send(Notification)` | Envía una notificación de forma síncrona |
| `CompletableFuture<NotificationResult> sendAsync(Notification)` | Envía de forma asíncrona |
| `List<NotificationResult> sendBatch(List<Notification>)` | Envía un lote de forma secuencial |
| `CompletableFuture<List<NotificationResult>> sendBatchAsync(List<Notification>)` | Envía un lote en paralelo |

### Dominio — `Notification` (sealed interface)

| Implementación | Campos | Canal |
|---|---|---|
| `EmailNotification` | `id`, `recipient`, `body`, `subject` | `EMAIL` |
| `SmsNotification` | `id`, `recipient`, `body` | `SMS` |
| `PushNotification` | `id`, `recipient`, `body`, `title` | `PUSH` |
| `SlackNotification` | `id`, `recipient`, `body`, `channelName` | `SLACK` |

### Value Objects

| Clase | Descripción |
|---|---|
| `NotificationId` | Identificador único (`NotificationId.generate()`) |
| `Recipient` | Destinatario (email, teléfono E.164, device token, canal Slack) |
| `MessageBody` | Cuerpo del mensaje |
| `ApiCredential` | Par key/secret con `toString()` enmascarado |

### `NotificationResult`

| Campo / Método | Descripción |
|---|---|
| `notificationId()` | ID de la notificación enviada |
| `status()` | `SUCCESS` o `FAILURE` |
| `channel()` | Canal utilizado |
| `providerName()` | Nombre del proveedor que procesó el envío |
| `timestamp()` | Momento del envío |
| `externalMessageId()` | ID externo simulado del proveedor |
| `metadata()` | Datos adicionales del proveedor |
| `isSuccess()` | `true` si `status == SUCCESS` |

### Contratos principales

| Interfaz | Responsabilidad |
|---|---|
| `NotificationProvider` | Envío por canal (`channel`, `supports`, `send`) |
| `NotificationValidator` | Validación pre-envío |
| `TemplateEngine` | Renderizado de placeholders |
| `RetryPolicy` | Política de reintentos |
| `NotificationEventPublisher` | Publicación de eventos (`onSuccess`, `onFailure`) |

### Pipeline interno

| Clase | Responsabilidad |
|---|---|
| `SendPipeline` | Orquesta: validate → template → resolve provider → retry → event |
| `ProviderRegistry` | Resuelve el proveedor por `NotificationChannel` |

### Excepciones

| Excepción | Cuándo se lanza |
|---|---|
| `ValidationException` | Datos de la notificación inválidos |
| `ConfigurationException` | Canal sin proveedor o builder sin canales |
| `ProviderException` | Error durante el envío |
| `RetryExhaustedException` | Reintentos agotados |

Todas extienden `NotificationException`.

---

## Seguridad

### Manejo de credenciales

`ApiCredential` enmascara automáticamente los secretos en logs y `toString()`:

```java
ApiCredential credential = new ApiCredential("my-api-key", "my-secret");
System.out.println(credential);
// Output: ApiCredential[key=****, secret=****]
```

### Mejores prácticas

| Práctica | Recomendación |
|---|---|
| **No hardcodear secretos** | Obtén credenciales de variables de entorno o un secret manager |
| **No loguear credenciales** | Los proveedores ya enmascaran `ApiCredential`; no imprimas keys manualmente |
| **No commitear secretos** | Usa `.gitignore` para archivos `.env` o configs locales |
| **Rotar credenciales** | Cambia API keys periódicamente; la librería permite reemplazar el proveedor sin cambiar el pipeline |
| **Principio de mínimo privilegio** | Usa credenciales con permisos solo de envío, no de administración |
| **Webhooks de Slack** | Trata la URL del webhook como un secreto; los logs la enmascaran parcialmente |

### Ejemplo seguro con variables de entorno

```java
String apiKey = System.getenv("SENDGRID_API_KEY");
String apiSecret = System.getenv("SENDGRID_API_SECRET");

NotificationClient client = NotificationClient.builder()
        .email(new EmailChannelConfig(
                new SendGridProvider(
                        new ApiCredential(apiKey, apiSecret),
                        System.getenv("EMAIL_FROM")
                ),
                System.getenv("EMAIL_FROM")
        ))
        .build();
```

### Lo que la librería NO hace

- No persiste credenciales
- No las transmite por red (proveedores simulados)
- No incluye cifrado de secretos en memoria

La responsabilidad de proteger las credenciales en tu aplicación es del consumidor de la librería.

---

## Módulos del proyecto

| Módulo | Artefacto Maven | Descripción |
|---|---|---|
| `notification-core` | `notification-core` | API pública, dominio, contratos, pipeline |
| `notification-providers` | `notification-providers` | Implementaciones simuladas de proveedores |
| `notification-examples` | `notification-examples` | Demo ejecutable con todos los canales |

## Decisiones arquitectónicas

- **Multi-módulo Maven:** el core no depende de proveedores concretos
- **Configuración Java pura:** sin frameworks de inyección ni archivos externos
- **Inmutabilidad:** dominio modelado con records y sealed interfaces
- **Extensibilidad:** nuevos proveedores implementando `NotificationProvider`
- **Maven Wrapper + Docker:** builds reproducibles sin dependencias locales
