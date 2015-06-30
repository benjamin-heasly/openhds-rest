package org.openhds.errors.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:error-log.properties")
public class ErrorLogProperties {

    @Value("${email.sendOnCreate}")
    private String sendOnCreate;

    @Value("${email.sendOnAnyUpdate}")
    private String sendOnAnyUpdate;

    @Value("${email.sendOnResolve}")
    private String sendOnResolve;

    @Value("${email.createMessageSubject}")
    private String createMessageSubject;

    @Value("${email.createMessageBody}")
    private String createMessageBody;

    @Value("${email.updateMessageSubject}")
    private String updateMessageSubject;

    @Value("${email.updateMessageBody}")
    private String updateMessageBody;

    @Value("${email.resolveMessageSubject}")
    private String resolveMessageSubject;

    @Value("${email.resolveMessageBody}")
    private String resolveMessageBody;

    @Value("${email.sendTo}")
    private String sendTo;

    public String isSendOnCreate() {
        return sendOnCreate;
    }

    public void setSendOnCreate(String sendOnCreate) {
        this.sendOnCreate = sendOnCreate;
    }

    public String isSendOnAnyUpdate() {
        return sendOnAnyUpdate;
    }

    public String isSendOnResolve() {
        return sendOnResolve;
    }

    public void setSendOnResolve(String sendOnResolve) {
        this.sendOnResolve = sendOnResolve;
    }

    public String getCreateMessageSubject() {
        return createMessageSubject;
    }

    public void setCreateMessageSubject(String createMessageSubject) {
        this.createMessageSubject = createMessageSubject;
    }

    public String getCreateMessageBody() {
        return createMessageBody;
    }

    public void setCreateMessageBody(String createMessageBody) {
        this.createMessageBody = createMessageBody;
    }

    public String getResolveMessageSubject() {
        return resolveMessageSubject;
    }

    public void setResolveMessageSubject(String resolveMessageSubject) {
        this.resolveMessageSubject = resolveMessageSubject;
    }

    public String getResolveMessageBody() {
        return resolveMessageBody;
    }

    public void setResolveMessageBody(String resolveMessageBody) {
        this.resolveMessageBody = resolveMessageBody;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
}
