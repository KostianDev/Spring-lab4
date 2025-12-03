<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Внести результат</title>
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
            max-width: 500px;
            margin: 0 auto;
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
            text-align: center;
        }
        .form-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }
        .game-info {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 25px;
            text-align: center;
        }
        .game-info .teams {
            font-size: 1.3em;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .game-info .vs {
            color: rgba(255,255,255,0.7);
            font-weight: normal;
        }
        .game-info .datetime {
            font-size: 0.9em;
            opacity: 0.9;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: 600;
        }
        input[type="number"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 1.2em;
            text-align: center;
            transition: border-color 0.3s;
        }
        input[type="number"]:focus {
            outline: none;
            border-color: #667eea;
        }
        .score-inputs {
            display: flex;
            gap: 20px;
            align-items: center;
            justify-content: center;
        }
        .score-inputs .score-group {
            flex: 1;
            text-align: center;
        }
        .score-inputs .separator {
            font-size: 2em;
            color: #999;
            padding-top: 25px;
        }
        .btn {
            padding: 12px 25px;
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
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background: #5a6268;
        }
        .form-actions {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
        }
        .back-link {
            margin-bottom: 20px;
            display: inline-block;
        }
        .existing-result {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            color: #155724;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="<c:url value='/schedule'/>" class="btn btn-secondary back-link">← До розкладу</a>
        
        <div class="form-card">
            <h1>📝 Внести результат</h1>
            
            <%-- Інформація про матч --%>
            <c:if test="${not empty game}">
                <div class="game-info">
                    <div class="teams">
                        <%-- Захист від XSS через c:out --%>
                        <c:out value='${game.homeTeam.name}'/>
                        <span class="vs">vs</span>
                        <c:out value='${game.awayTeam.name}'/>
                    </div>
                    <div class="datetime">
                        📅 <c:out value='${game.dateTime}'/>
                    </div>
                </div>
            </c:if>
            
            <%-- Умовне форматування: if - показуємо існуючий результат --%>
            <c:if test="${not empty result}">
                <div class="existing-result">
                    ✓ Поточний результат: <strong><c:out value='${result.homeScore}'/> : <c:out value='${result.awayScore}'/></strong>
                </div>
            </c:if>
            
            <form action="<c:url value='/schedule/result/${game.id}'/>" method="post">
                <div class="score-inputs">
                    <div class="score-group">
                        <label for="homeScore">
                            <c:out value='${game.homeTeam.name}'/>
                        </label>
                        <input type="number" id="homeScore" name="homeScore" min="0" max="99" required
                               value="${not empty result ? result.homeScore : '0'}">
                    </div>
                    <div class="separator">:</div>
                    <div class="score-group">
                        <label for="awayScore">
                            <c:out value='${game.awayTeam.name}'/>
                        </label>
                        <input type="number" id="awayScore" name="awayScore" min="0" max="99" required
                               value="${not empty result ? result.awayScore : '0'}">
                    </div>
                </div>
                
                <div class="form-actions">
                    <a href="<c:url value='/schedule'/>" class="btn btn-secondary">Скасувати</a>
                    <button type="submit" class="btn btn-primary">
                        <%-- Умовне форматування: різний текст кнопки --%>
                        <c:choose>
                            <c:when test="${not empty result}">💾 Оновити результат</c:when>
                            <c:otherwise>💾 Зберегти результат</c:otherwise>
                        </c:choose>
                    </button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
