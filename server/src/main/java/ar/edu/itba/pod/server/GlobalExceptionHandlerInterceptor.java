package ar.edu.itba.pod.server;


import ar.edu.itba.pod.server.exception.*;
import com.google.rpc.Code;
import io.grpc.*;
import io.grpc.protobuf.StatusProto;

import java.util.Map;


public class GlobalExceptionHandlerInterceptor implements ServerInterceptor {

    @Override
    public <T, R> ServerCall.Listener<T> interceptCall(
            ServerCall<T, R> serverCall, Metadata headers, ServerCallHandler<T, R> serverCallHandler) {
        ServerCall.Listener<T> delegate = serverCallHandler.startCall(serverCall, headers);
        return new ExceptionHandler<>(delegate, serverCall, headers);
    }

    private static class ExceptionHandler<T, R> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<T> {

        private final ServerCall<T, R> delegate;
        private final Metadata headers;

        ExceptionHandler(ServerCall.Listener<T> listener, ServerCall<T, R> serverCall, Metadata headers) {
            super(listener);
            this.delegate = serverCall;
            this.headers = headers;
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException ex) {
                handleException(ex, delegate, headers);
            }
        }

        private final Map<Class<? extends Throwable>, Code> errorCodesByException = Map.ofEntries(
                Map.entry(DoctorAlreadyExistsException.class, Code.ALREADY_EXISTS),
                Map.entry(DoctorAlreadyRegisteredException.class, Code.ALREADY_EXISTS),
                Map.entry(DoctorDidNotRegisterException.class, Code.NOT_FOUND),
                Map.entry(DoctorIsAttendingException.class, Code.FAILED_PRECONDITION),
                Map.entry(DoctorNotFoundException.class, Code.NOT_FOUND),
                Map.entry(InvalidEmergencyLevelException.class, Code.INVALID_ARGUMENT),
                Map.entry(InvalidPatientDoctorPairException.class, Code.INVALID_ARGUMENT),
                Map.entry(NoDischargedPatientsException.class, Code.FAILED_PRECONDITION),
                Map.entry(NoPatientsInWaitRoomException.class, Code.FAILED_PRECONDITION),
                Map.entry(NoRoomsException.class, Code.FAILED_PRECONDITION),
                Map.entry(PatientAlreadyExistsException.class, Code.ALREADY_EXISTS),
                Map.entry(PatientNotFoundException.class, Code.NOT_FOUND),
                Map.entry(RoomNotFoundException.class, Code.NOT_FOUND)
        );

        private void handleException(RuntimeException exception, ServerCall<T, R> serverCall, Metadata headers) {
            Throwable error = exception;
            if (!errorCodesByException.containsKey(error.getClass())) {
                // Si la excepción vino "wrappeada" entonces necesitamos preguntar por la causa.
                error = error.getCause();
                if (error == null || !errorCodesByException.containsKey(error.getClass())) {
                    // Una excepción NO esperada.
                    serverCall.close(Status.UNKNOWN, headers);
                    return;
                }
            }
            // Una excepción esperada.
            com.google.rpc.Status rpcStatus = com.google.rpc.Status.newBuilder()
                    .setCode(errorCodesByException.get(error.getClass()).getNumber())
                    .setMessage(error.getMessage())
                    .build();
            StatusRuntimeException statusRuntimeException = StatusProto.toStatusRuntimeException(rpcStatus);
            Status newStatus = Status.fromThrowable(statusRuntimeException);
            serverCall.close(newStatus, headers);
        }
    }
}
