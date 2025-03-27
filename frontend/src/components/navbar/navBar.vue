<script setup lang="ts">

</script>

<template>
  <header class="nav">
    <div class="logo">
      <RouterLink to="/browse"></RouterLink>
      </div>
    <nav>
      <RouterLink to="Home"></RouterLink>
    </nav>

  </header>
</template>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
}
.nav .logo a {
  font-weight: bold;
  color: black;
}
nav a {
  color: #333;
}
</style>
