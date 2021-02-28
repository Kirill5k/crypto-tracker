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
      :state="false"
      class="date-range__date-picker"
      today-button
      reset-button
    />
    <label
      for="date-to"
      class="date-range__label"
    >
      Date to:
    </label>
    <b-form-datepicker
      id="date-to"
      :state="true"
      class="date-range__date-picker"
      today-button
      reset-button
    />
    <b-button
      variant="primary"
      class="ml-2"
    >
      Show
    </b-button>
  </div>
</template>

<script>
import { BFormDatepicker, BButton } from 'bootstrap-vue'

export default {
  name: 'DateRange',
  components: { BFormDatepicker, BButton },
  props: {
    msg: String
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
