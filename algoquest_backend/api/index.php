<?php
// ========================================================
// AlgoQuest — Primary REST API Router
// Front controller supporting Apache XAMPP & PHP CLI Server
// ========================================================

// 1. Centralized Error and Exception Handling
error_reporting(E_ALL);
ini_set('display_errors', '0');

set_exception_handler(function (Throwable $e) {
    http_response_code(500);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'success' => false,
        'message' => 'An unexpected server error occurred.',
        'errors' => ['server' => 'Internal Server Error']
    ]);
    exit;
});

// 2. CORS Headers
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

// 3. Load Dependencies
require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../controllers/AuthController.php';
require_once __DIR__ . '/../controllers/DashboardController.php';
require_once __DIR__ . '/../controllers/LearningController.php';
require_once __DIR__ . '/../controllers/QuizController.php';
require_once __DIR__ . '/../controllers/ProfileController.php';
require_once __DIR__ . '/../controllers/AiChatController.php';

// 4. Request Parsing
$method = $_SERVER['REQUEST_METHOD'];
$rawInput = file_get_contents('php://input');
$body = json_decode($rawInput, true) ?: [];

// Extract route path: normalize /algoquest_backend/api/v1/... or /api/v1/...
$uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$scriptName = dirname($_SERVER['SCRIPT_NAME']);
if (strpos($uri, $scriptName) === 0) {
    $uri = substr($uri, strlen($scriptName));
}
$uri = trim($uri, '/');

// Support fallback ?route= parameter if mod_rewrite is disabled
if (isset($_GET['route'])) {
    $uri = trim($_GET['route'], '/');
}

// Normalize /v1 prefix
if (str_starts_with($uri, 'api/v1/')) {
    $path = substr($uri, 7);
} elseif (str_starts_with($uri, 'v1/')) {
    $path = substr($uri, 3);
} elseif (str_starts_with($uri, 'api/')) {
    $path = substr($uri, 4);
} else {
    $path = $uri;
}

// 5. REST Route Matcher
switch (true) {
    // Health Check
    case ($path === '' || $path === 'health') && $method === 'GET':
        Response::success('AlgoQuest API is operational.', [
            'version' => '1.0.0',
            'timestamp' => date('Y-m-d H:i:s'),
            'status' => 'ONLINE'
        ]);
        break;

    // --- Authentication ---
    case $path === 'auth/register' && $method === 'POST':
        AuthController::register($body);
        break;

    case $path === 'auth/login' && $method === 'POST':
        AuthController::login($body);
        break;

    case $path === 'auth/logout' && $method === 'POST':
        AuthController::logout();
        break;

    case $path === 'auth/me' && $method === 'GET':
        AuthController::me();
        break;

    case $path === 'user/personalize' && $method === 'POST':
        AuthController::personalize($body);
        break;

    // --- Dashboard ---
    case $path === 'dashboard' && $method === 'GET':
        DashboardController::index();
        break;

    // --- Learning Topics & Lessons ---
    case $path === 'topics' && $method === 'GET':
        LearningController::getTopics();
        break;

    case preg_match('#^topics/(\d+)$#', $path, $matches) && $method === 'GET':
        LearningController::getTopicDetail((int)$matches[1]);
        break;

    case preg_match('#^lessons/(\d+)/complete$#', $path, $matches) && $method === 'POST':
        LearningController::completeLesson((int)$matches[1]);
        break;

    case $path === 'algorithms' && $method === 'GET':
        LearningController::getAlgorithms();
        break;

    // --- Quiz & Mastery ---
    case preg_match('#^quizzes/lesson/(\d+)$#', $path, $matches) && $method === 'GET':
        QuizController::getQuiz((int)$matches[1]);
        break;

    case preg_match('#^quizzes/(\d+)/submit$#', $path, $matches) && $method === 'POST':
        QuizController::submitQuiz((int)$matches[1], $body);
        break;

    // --- Profile & Achievements ---
    case $path === 'profile' && $method === 'GET':
        ProfileController::getProfile();
        break;

    case $path === 'profile' && $method === 'PUT':
        ProfileController::updateProfile($body);
        break;

    // --- AI Ally Chat ---
    case $path === 'ai/chat' && $method === 'POST':
        AiChatController::chat($body);
        break;

    case $path === 'ai/history' && $method === 'GET':
        AiChatController::getHistory();
        break;

    default:
        Response::notFound("Endpoint not found: [{$method}] /{$path}");
        break;
}

