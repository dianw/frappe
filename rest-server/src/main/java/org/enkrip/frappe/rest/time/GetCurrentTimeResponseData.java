package org.enkrip.frappe.rest.time;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;

@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface GetCurrentTimeResponseData extends Serializable {
    String getCurrentTimeISO();
}
