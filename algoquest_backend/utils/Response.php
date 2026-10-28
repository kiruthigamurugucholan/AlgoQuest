<?php
// ========================================================
// AlgoQuest — Standardized JSON Response Utility
// ========================================================

class Response {
    public static function json(bool $success, string $message, $data = null, array $errors = [], int $statusCode = 200): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        $response = [
            'success' => $success,
            'message' => $message,
        ];

        if ($data !== null) {
            $response['data'] = $data;
        }

        if (!empty($errors)) {
            $response['errors'] = $errors;
        }

        echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
        exit;
    }

    public static function success(string $message = 'Success', $data = null, int $statusCode = 200): void {
        self::json(true, $message, $data, [], $statusCode);
    }

    public static function error(string $message = 'Error', array $errors = [], int $statusCode = 400): void {
        self::json(false, $message, null, $errors, $statusCode);
    }

    public static function unauthorized(string $message = 'Authentication required'): void {
        self::json(false, $message, null, ['auth' => 'Invalid or missing bearer token'], 401);
    }

    public static function forbidden(string $message = 'Forbidden'): void {
        self::json(false, $message, null, ['auth' => 'Access denied'], 403);
    }

    public static function notFound(string $message = 'Resource not found'): void {
        self::json(false, $message, null, ['resource' => 'Not found'], 404);
    }
}

