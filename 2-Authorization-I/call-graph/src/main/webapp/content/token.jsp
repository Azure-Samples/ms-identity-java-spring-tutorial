<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card">
    <h5 class="card-header bg-primary">
        ID Token Details
    </h5>
    <div class="card-body">
        <p class="card-text">
            The following is a limited subset of some important claims in your ID Token.
            <br>
            <c:forEach items="${claims}" var="claim">
                <strong>${claim.key}:</strong> ${claim.value}
                <br>
            </c:forEach>
            <br>
            <a class="btn btn-success" href="/sign_in_status">Sign-in Status</a>
            <a class="btn btn-success" href="/call_graph">Call Graph</a>
        </p>
    </div>
</div>
