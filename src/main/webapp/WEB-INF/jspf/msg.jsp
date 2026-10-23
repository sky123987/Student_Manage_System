<c:if test="${not empty msg}">
  <div class="alert alert-${empty msgType ? 'info' : msgType} alert-dismissible fade show" role="alert">
    <c:out value="${msg}"/>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
</c:if>