<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редагувати матч</title>
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
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: 600;
        }
        select, input[type="datetime-local"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 1em;
            transition: border-color 0.3s;
        }
        select:focus, input[type="datetime-local"]:focus {
            outline: none;
            border-color: #667eea;
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
        .game-id {
            background: #f8f9fa;
            padding: 10px;
            border-radius: 5px;
            color: #666;
            font-size: 0.9em;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="<c:url value='/schedule'/>" class="btn btn-secondary back-link">← До розкладу</a>
        
        <div class="form-card">
            <h1>✏️ Редагувати матч</h1>
            
            <%-- Умовне форматування: if - показуємо ID матчу якщо є --%>
            <c:if test="${not empty game}">
                <div class="game-id">
                    ID матчу: <c:out value='${game.id}'/>
                </div>
            </c:if>
            
            <form action="<c:url value='/schedule/edit/${game.id}'/>" method="post">
                <div class="form-group">
                    <label for="homeTeamId">Господарі:</label>
                    <select id="homeTeamId" name="homeTeamId" required>
                        <%-- Цикл forEach з умовним вибором --%>
                        <c:forEach var="team" items="${teams}">
                            <option value="${team.id}" 
                                <c:if test="${team.id == game.homeTeam.id}">selected</c:if>>
                                <c:out value='${team.name}'/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="awayTeamId">Гості:</label>
                    <select id="awayTeamId" name="awayTeamId" required>
                        <%-- Цикл forEach з умовним вибором --%>
                        <c:forEach var="team" items="${teams}">
                            <option value="${team.id}"
                                <c:if test="${team.id == game.awayTeam.id}">selected</c:if>>
                                <c:out value='${team.name}'/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="dateTime">Дата та час:</label>
                    <input type="datetime-local" id="dateTime" name="dateTime" required
                           value="${game.dateTime}">
                </div>
                
                <div class="form-actions">
                    <a href="<c:url value='/schedule'/>" class="btn btn-secondary">Скасувати</a>
                    <button type="submit" class="btn btn-primary">💾 Зберегти зміни</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        // Перевірка що обрані різні команди
        document.querySelector('form').addEventListener('submit', function(e) {
            var homeTeam = document.getElementById('homeTeamId').value;
            var awayTeam = document.getElementById('awayTeamId').value;
            if (homeTeam === awayTeam) {
                e.preventDefault();
                alert('Команда не може грати сама з собою!');
            }
        });
    </script>
</body>
</html>
