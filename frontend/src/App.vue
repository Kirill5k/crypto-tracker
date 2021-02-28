<template>
  <div id="app">
    <b-navbar type="dark" variant="dark">
        <b-navbar-nav>
          <b-nav-item to="/">Home</b-nav-item>
        </b-navbar-nav>
      </b-navbar>
    <router-view/>
  </div>
</template>

<script>
import { BNavbar, BNavbarNav, BNavItem } from 'bootstrap-vue'

export default {
  name: 'App',
  components: { BNavbar, BNavbarNav, BNavItem }
}
</script>

<style lang="scss">
@import '~bootstrap/scss/bootstrap.scss';
@import '~bootstrap-vue/src/index.scss';

</style>
