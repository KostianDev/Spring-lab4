<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Команди</title>
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
            max-width: 900px;
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
        .team-actions {
            display: flex;
            gap: 5px;
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
            font-style: italic;
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
        /* Умовне форматування для кількості команд */
        .team-count {
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 0.9em;
            font-weight: bold;
        }
        .count-low {
            background: #fff3cd;
            color: #856404;
        }
        .count-medium {
            background: #d4edda;
            color: #155724;
        }
        .count-high {
            background: #cce5ff;
            color: #004085;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="<c:url value='/'/>" class="btn btn-secondary back-link">← На головну</a>
        
        <h1>📋 Список команд</h1>
        
        <div class="actions">
            <form action="<c:url value='/teams/search'/>" method="get" class="search-form">
                <input type="text" name="name" placeholder="Пошук за назвою..." 
                       value="<c:out value='${search}'/>">
                <button type="submit" class="btn btn-primary">🔍 Шукати</button>
            </form>
            <a href="<c:url value='/teams/new'/>" class="btn btn-success">+ Додати команду</a>
        </div>
        
        <%-- Умовне форматування: if - показуємо інформацію про пошук --%>
        <c:if test="${not empty search}">
            <div class="search-info">
                <span>Результати пошуку: "<c:out value='${search}'/>"</span>
                <a href="<c:url value='/teams'/>" class="btn btn-secondary">Скинути</a>
            </div>
        </c:if>
        
        <%-- Умовне форматування: choose/when для відображення кількості команд --%>
        <p style="margin-bottom: 15px;">
            Всього команд: 
            <c:choose>
                <c:when test="${teams.size() == 0}">
                    <span class="team-count count-low">0 (немає команд)</span>
                </c:when>
                <c:when test="${teams.size() < 5}">
                    <span class="team-count count-low"><c:out value='${teams.size()}'/> (мало)</span>
                </c:when>
                <c:when test="${teams.size() < 10}">
                    <span class="team-count count-medium"><c:out value='${teams.size()}'/> (достатньо)</span>
                </c:when>
                <c:otherwise>
                    <span class="team-count count-high"><c:out value='${teams.size()}'/> (багато)</span>
                </c:otherwise>
            </c:choose>
        </p>
        
        <%-- Умовне форматування: вибір між таблицею та повідомленням --%>
        <c:choose>
            <c:when test="${empty teams}">
                <div class="no-data">
                    <p>Команд поки немає. Додайте першу команду!</p>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Назва команди</th>
                            <th>Дії</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- Цикл forEach для виводу команд --%>
                        <c:forEach var="team" items="${teams}" varStatus="status">
                            <tr>
                                <td><c:out value='${team.id}'/></td>
                                <td>
                                    <%-- Захист від XSS через c:out --%>
                                    <c:out value='${team.name}'/>
                                    <%-- Умовне форматування: позначка для першої команди --%>
                                    <c:if test="${status.first}">
                                        <span style="color: #667eea; font-size: 0.8em;">(перша)</span>
                                    </c:if>
                                </td>
                                <td class="team-actions">
                                    <a href="<c:url value='/teams/edit/${team.id}'/>" class="btn btn-warning">✏️ Редагувати</a>
                                    <form action="<c:url value='/teams/delete/${team.id}'/>" method="post" style="display:inline;" 
                                          onsubmit="return confirm('Ви впевнені, що хочете видалити цю команду?');">
                                        <button type="submit" class="btn btn-danger">🗑️ Видалити</button>
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
