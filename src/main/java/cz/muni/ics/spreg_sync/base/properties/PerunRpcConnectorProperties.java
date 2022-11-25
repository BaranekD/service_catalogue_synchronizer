package cz.muni.ics.spreg_sync.base.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Slf4j

@Validated
@Component
@ConfigurationProperties(prefix = "connector.rpc")
public class PerunRpcConnectorProperties {

    @NotBlank
    private String url;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String serializer;
    @Min(1)
    private int connectionTimeout;
    @Min(1)
    private int connectionRequestTimeout;
    @Min(1)
    private int requestTimeout;
    private boolean enabled;

}
