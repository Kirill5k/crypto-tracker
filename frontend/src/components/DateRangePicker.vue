<template>
  <div class="date-range">
    <label
      for="date-from"
      class="date-range__label"
    >
      Date from:
    </label>
    <b-form-datepicker
      id="date-from"
      :state="isValidDateFrom"
      class="date-range__date-picker"
      today-button
      reset-button
      v-model="dateFrom"
    />
    <label
      for="date-to"
      class="date-range__label"
    >
      Date to:
    </label>
    <b-form-datepicker
      id="date-to"
      :state="isValidDateTo"
      class="date-range__date-picker"
      today-button
      reset-button
      v-model="dateTo"
    />
    <b-button
      variant="primary"
      class="ml-2"
      :disabled="!isValidDateFrom || !isValidDateTo"
      @click="show"
    >
      Show
    </b-button>
  </div>
</template>

<script>
import { BFormDatepicker, BButton } from 'bootstrap-vue'

export default {
  name: 'DateRangePicker',
  components: { BFormDatepicker, BButton },
  props: {
    msg: String
  },
  data () {
    return {
      dateFrom: new Date(),
      dateTo: new Date()
    }
  },
  computed: {
    isValidDateFrom () {
      if (!this.dateFrom) {
        return false
      } else if (this.dateFrom && !this.dateTo) {
        return true
      } else {
        return this.dateFrom <= this.dateTo
      }
    },
    isValidDateTo () {
      if (!this.dateTo) {
        return false
      } else if (!this.dateFrom && this.dateTo) {
        return true
      } else {
        return this.dateTo >= this.dateFrom
      }
    }
  },
  methods: {
    show () {
      const from = new Date(this.dateFrom.getFullYear(), this.dateFrom.getMonth(), this.dateFrom.getDate(), 0, 0, 0)
      const to = new Date(this.dateTo.getFullYear(), this.dateTo.getMonth(), this.dateTo.getDate(), 23, 59, 59)
      this.$emit('show', { dateFrom: from, dateTo: to })
    }
  }
}
</script>

<style scoped lang="scss">
.date-range {
  display: flex;
  margin-top: 20px;

  &__label {
    width: 90px;
    text-align: right;
    padding-right: 10px;
  }

  &__date-picker {
    width: 350px;
  }
}
</style>
