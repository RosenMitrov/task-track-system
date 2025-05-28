package http.tasktracksystem.domain.utils.responses;

import http.tasktracksystem.domain.dtos.responses.ApiResponse;

public final class GeneralUtils {

    public static <T> ApiResponse<T> buildApiResponse(String messageFormat,
                                                      T data) {
        return ApiResponse.<T>builder()
                .message(messageFormat)
                .data(data)
                .build();
    }

    public static String formatRow(String messageFormat,
                                   Object... messageValues) {
        return messageFormat.formatted(messageValues);
    }

    public static <T> ApiResponse<T> buildApiResponse(String rowMessage) {
        return ApiResponse.<T>builder()
                .message(rowMessage)
                .build();
    }

    public static <T> ApiResponse<T> buildApiResponse(String messageFormat,
                                                      Object... messageValues) {
        return ApiResponse.<T>builder()
                .message(messageFormat.formatted(messageValues))
                .build();
    }
}
