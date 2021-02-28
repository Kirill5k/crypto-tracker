<template>
  <b-container>
    <date-range/>
  </b-container>
</template>

<script>
import { BContainer } from 'bootstrap-vue'
import DateRange from '@/components/DateRange.vue'

export default {
  name: 'Home',
  components: { BContainer, DateRange }
}
</script>
