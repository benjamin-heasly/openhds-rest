package org.openhds.errors.util;

import org.openhds.domain.model.FieldWorker;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorLogUtil {

    public static ErrorLog generateErrorLog(String assignedTo,
                                            String dataPayload,
                                            ZonedDateTime dateOfResolution,
                                            String entityType,
                                            FieldWorker fieldWorker,
                                            String resolutionStatus,
                                            List<String> errors) {

        ErrorLog errorLog = new ErrorLog();

        errorLog.setAssignedTo(assignedTo);
        errorLog.setDataPayload(dataPayload);
        errorLog.setDateOfResolution(dateOfResolution);
        errorLog.setEntityType(entityType);
        errorLog.setCollectedBy(fieldWorker);
        errorLog.setResolutionStatus(resolutionStatus);
        errorLog.setErrors(generateErrors(errors));

        return errorLog;
    }

    private static List<Error> generateErrors(List<String> errors) {
        List<Error> objectErrors = new ArrayList<>();

        for (String error : errors) {
            Error objectError = new Error(error);
            objectErrors.add(objectError);
        }

        return objectErrors;
    }
}
