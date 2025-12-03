<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Розклад матчів</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }
        .container {
            max-width: 1100px;
            margin: 0 auto;
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
            text-align: center;
        }
        .actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 10px;
        }
        .search-form {
            display: flex;
            gap: 10px;
        }
        .search-form input {
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 1em;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1em;
            text-decoration: none;
            display: inline-block;
            transition: background 0.3s;
        }
        .btn-primary {
            background: #667eea;
            color: white;
        }
        .btn-primary:hover {
            background: #5a6fd6;
        }
        .btn-success {
            background: #28a745;
            color: white;
        }
        .btn-success:hover {
            background: #218838;
        }
        .btn-warning {
            background: #ffc107;
            color: #333;
        }
        .btn-warning:hover {
            background: #e0a800;
        }
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        .btn-danger:hover {
            background: #c82333;
        }
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background: #5a6268;
        }
        .btn-info {
            background: #17a2b8;
            color: white;
        }
        .btn-info:hover {
            background: #138496;
        }
        .btn-sm {
            padding: 5px 10px;
            font-size: 0.85em;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        th {
            background: #667eea;
            color: white;
        }
        tr:hover {
            background: #f8f9fa;
        }
        .game-actions {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
            font-style: italic;
            background: white;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }
        .search-info {
            background: #e7f3ff;
            padding: 10px 15px;
            border-radius: 5px;
            margin-bottom: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .back-link {
            margin-bottom: 20px;
            display: inline-block;
        }
        .score {
            font-weight: bold;
            font-size: 1.1em;
        }
        .score-win {
            color: #28a745;
        }
        .score-lose {
            color: #dc3545;
        }
        .score-draw {
            color: #ffc107;
        }
        .no-result {
            color: #999;
            font-style: italic;
        }
        .game-status {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 10px;
            font-size: 0.8em;
            font-weight: bold;
        }
        .status-scheduled {
            background: #e7f3ff;
            color: #004085;
        }
        .status-completed {
            background: #d4edda;
            color: #155724;
        }
        .vs {
            color: #999;
            font-weight: normal;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="<c:url value='/'/>" class="btn btn-secondary back-link">← На головну</a>
        
        <h1>📅 Розклад матчів</h1>
        
        <div class="actions">
            <form action="<c:url value='/schedule'/>" method="get" class="search-form">
                <input type="text" name="search" placeholder="Пошук за командою..." 
                       value="<c:out value='${search}'/>">
                <button type="submit" class="btn btn-primary">🔍 Шукати</button>
            </form>
            <a href="<c:url value='/schedule/new'/>" class="btn btn-success">+ Додати матч</a>
        </div>
        
        <%-- Умовне форматування: if - показуємо інформацію про пошук --%>
        <c:if test="${not empty search}">
            <div class="search-info">
                <span>Результати пошуку: "<c:out value='${search}'/>"</span>
                <a href="<c:url value='/schedule'/>" class="btn btn-secondary">Скинути</a>
            </div>
        </c:if>
        
        <%-- Умовне форматування: choose/when для відображення таблиці або повідомлення --%>
        <c:choose>
            <c:when test="${empty games}">
                <div class="no-data">
                    <p>Матчів поки немає. Додайте перший матч!</p>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Дата та час</th>
                            <th>Матч</th>
                            <th>Результат</th>
                            <th>Статус</th>
                            <th>Дії</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- Цикл forEach для виводу матчів --%>
                        <c:forEach var="game" items="${games}">
                            <tr>
                                <td><c:out value='${game.id}'/></td>
                                <td><c:out value='${game.dateTime}'/></td>
                                <td>
                                    <%-- Захист від XSS через c:out --%>
                                    <strong><c:out value='${game.homeTeam.name}'/></strong>
                                    <span class="vs">vs</span>
                                    <strong><c:out value='${game.awayTeam.name}'/></strong>
                                </td>
                                <td>
                                    <%-- Пошук результату для цього матчу --%>
                                    <c:set var="gameResult" value="${null}"/>
                                    <c:forEach var="r" items="${results}">
                                        <c:if test="${r.game.id == game.id}">
                                            <c:set var="gameResult" value="${r}"/>
                                        </c:if>
                                    </c:forEach>
                                    
                                    <%-- Умовне форматування: choose/when для відображення результату --%>
                                    <c:choose>
                                        <c:when test="${gameResult != null}">
                                            <%-- Визначаємо тип результату для стилізації --%>
                                            <c:choose>
                                                <c:when test="${gameResult.homeScore > gameResult.awayScore}">
                                                    <span class="score score-win">
                                                        <c:out value='${gameResult.homeScore}'/> : <c:out value='${gameResult.awayScore}'/>
                                                    </span>
                                                    <small>(перемога господарів)</small>
                                                </c:when>
                                                <c:when test="${gameResult.homeScore < gameResult.awayScore}">
                                                    <span class="score score-lose">
                                                        <c:out value='${gameResult.homeScore}'/> : <c:out value='${gameResult.awayScore}'/>
                                                    </span>
                                                    <small>(перемога гостей)</small>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="score score-draw">
                                                        <c:out value='${gameResult.homeScore}'/> : <c:out value='${gameResult.awayScore}'/>
                                                    </span>
                                                    <small>(нічия)</small>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="no-result">Очікується</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <%-- Умовне форматування: if/else для статусу --%>
                                    <c:choose>
                                        <c:when test="${gameResult != null}">
                                            <span class="game-status status-completed">✓ Завершено</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="game-status status-scheduled">⏳ Заплановано</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="game-actions">
                                    <a href="<c:url value='/schedule/result/${game.id}'/>" class="btn btn-info btn-sm">
                                        <%-- Умовне форматування: різний текст кнопки --%>
                                        <c:choose>
                                            <c:when test="${gameResult != null}">✏️ Змінити рахунок</c:when>
                                            <c:otherwise>📝 Внести рахунок</c:otherwise>
                                        </c:choose>
                                    </a>
                                    <a href="<c:url value='/schedule/edit/${game.id}'/>" class="btn btn-warning btn-sm">✏️ Редагувати</a>
                                    <form action="<c:url value='/schedule/delete/${game.id}'/>" method="post" style="display:inline;"
                                          onsubmit="return confirm('Ви впевнені, що хочете видалити цей матч?');">
                                        <button type="submit" class="btn btn-danger btn-sm">🗑️ Видалити</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
